#!/usr/bin/env bash

set -Eeuo pipefail

readonly SCRIPT_NAME="$(basename "$0")"

remote="origin"
tag=""
tag_message=""
dry_run=false

print_usage() {
    printf '%s\n' \
        "用法：" \
        "  ${SCRIPT_NAME} [--remote <name>] [--message <text>] [--dry-run] <tag>" \
        "" \
        "示例：" \
        "  ${SCRIPT_NAME} v0.5.0" \
        "  ${SCRIPT_NAME} --remote origin --message \"发布 v0.5.0\" v0.5.0" \
        "  ${SCRIPT_NAME} --dry-run v0.5.0" \
        "" \
        "脚本会依次检查 Git 仓库、工作区、远程分支、完整测试和标签冲突，" \
        "全部通过后才会创建带说明的 tag 并推送到远程。"
}

info() {
    printf '[release-tag] %s\n' "$*"
}

fail() {
    printf '[release-tag] 错误：%s\n' "$*" >&2
    exit 1
}

while (($# > 0)); do
    case "$1" in
        --remote)
            (($# >= 2)) || fail "--remote 需要指定远程名称"
            remote="$2"
            shift 2
            ;;
        --message)
            (($# >= 2)) || fail "--message 需要指定标签说明"
            tag_message="$2"
            shift 2
            ;;
        --dry-run)
            dry_run=true
            shift
            ;;
        -h|--help)
            print_usage
            exit 0
            ;;
        -*)
            fail "未知选项：$1"
            ;;
        *)
            [[ -z "$tag" ]] || fail "一次只能指定一个 tag"
            tag="$1"
            shift
            ;;
    esac
done

[[ -n "$tag" ]] || {
    print_usage
    fail "必须指定要发布的 tag"
}

readonly SEMVER_PATTERN='^v(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)(-[0-9A-Za-z]+([.-][0-9A-Za-z]+)*)?(\+[0-9A-Za-z]+([.-][0-9A-Za-z]+)*)?$'
[[ "$tag" =~ $SEMVER_PATTERN ]] || fail "tag 必须使用语义化版本格式，例如 v0.5.0 或 v0.5.0-rc.1"
[[ "$remote" =~ ^[A-Za-z0-9._/]+$ ]] || fail "远程名称包含不支持的字符：$remote"

command -v git >/dev/null 2>&1 || fail "未安装 Git，或者 Git 不在 PATH 中"

repo_root="$(git rev-parse --show-toplevel 2>/dev/null)" || fail "当前目录不在 Git 仓库中"
cd "$repo_root"

[[ -x "./gradlew" ]] || fail "Gradle Wrapper 不存在或不可执行：${repo_root}/gradlew"
git remote get-url "$remote" >/dev/null 2>&1 || fail "Git 远程 '$remote' 不存在"

branch="$(git symbolic-ref --quiet --short HEAD)" || fail "不允许在 detached HEAD 状态下发布标签"
head_commit="$(git rev-parse HEAD)"

[[ -z "$(git status --porcelain)" ]] || fail "工作区不干净，请先提交或暂存全部修改"
git show-ref --verify --quiet "refs/tags/$tag" && fail "本地 tag '$tag' 已存在"

info "正在检查远程分支 '$remote/$branch'"
remote_branch_output="$(git ls-remote --heads "$remote" "refs/heads/$branch")" \
    || fail "无法查询远程分支 '$remote/$branch'"
[[ -n "$remote_branch_output" ]] || fail "远程分支 '$remote/$branch' 不存在"
remote_head="${remote_branch_output%%$'\t'*}"
[[ "$head_commit" == "$remote_head" ]] \
    || fail "当前 HEAD 与 '$remote/$branch' 不一致，请先推送或同步分支"

remote_tag_output="$(git ls-remote --tags --refs "$remote" "refs/tags/$tag")" \
    || fail "无法查询远程 '$remote' 上的 tag '$tag'"
[[ -z "$remote_tag_output" ]] || fail "远程 tag '$remote/$tag' 已存在"

info "正在运行完整测试"
./gradlew cleanTest test

if [[ -z "$tag_message" ]]; then
    tag_message="发布 $tag"
fi

if [[ "$dry_run" == true ]]; then
    info "演练通过；可以从提交 $head_commit 创建 tag '$tag'"
    exit 0
fi

info "正在创建带说明的 tag '$tag'"
git tag --annotate "$tag" --message "$tag_message" "$head_commit"

info "正在把 tag '$tag' 推送到远程 '$remote'"
if ! git push "$remote" "refs/tags/$tag:refs/tags/$tag"; then
    fail "推送失败；本地 tag '$tag' 已保留，可以修复问题后重试推送"
fi

info "发布 tag '$tag' 已创建并成功推送"
