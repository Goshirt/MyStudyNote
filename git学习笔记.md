## git 常用命令

- `git status -s `  得到一种更为紧凑的格式输出
- `git log --stat` 看到每次提交的简略的统计信息
- `git log --pretty=oneline` 显示最近到最远的提交日志 - `-pretty=oneline` 每一次提交用一行显示
- `git rm <file>` 从已跟踪文件清单中移除（确切地说，是从暂存区域移除）以后就不会出现在未跟踪文件清单中了
- `git rm --cached <file>` 让文件保留在磁盘，但是并不想让 Git 继续跟踪
- `git mv <name1> <name2>`  重命名
- `git reset --hard <xxxx> ` 回退到指定的版本号
- `git reflog` 查看历史命令
- `git diff HEAD -- <fileName>` 指定文件工作区和版本库里面最新版本的区别
- `git diff <fileName>` 查看指定文件与本地仓库最新版本的区别
- `git diff --cached`
- `git remote -v` 显示远程仓库地址信息
- `git remote add <shortname> <url>` 添加一个新的远程git 仓库
- `git remote show` 显示远程仓库的详细信息
- `git fetch origin` 拉取 从远程仓库中拉取信息到本地仓库
- `git push origin master` 推送本地仓库信息到远程仓库的master分支上
- `git log -p master origin/master` 比较本地仓库的master分支与远程仓库master分支的差别
- `git branch <name>` 创建分支
- `git branch -d <name>` 删除分支
- `git checkout <name>` 切换到指定分支，切换前最好使用`git commit `提交上一个分支代码，确保暂存区干净
- `git merge <name>` 将指定的分支合并到当前分支
- `git branch --merged ` 查看被合并到当前分支的分支列表，如果列表中分支名字前没有 * 号的分支表示已经完全合并到了当前分支
- `git branch --no-merged` 查看未被合并到当前分支的分支列表
- `git rebase master ` 将当前分支变基到master中
- `git config --global core.autocrlf false` 全局关闭自动转换功能 ，防止自动转换不同操作系统的换行符
- `git checkout -- fileName ` 撤销修改






	
	git push origin --delete serverfix     删除远程服务器的serverfix分支
	
	git rebase master    
		将当前的分支的修改复制到master分支，然后再使用merge，和使用 merge 命令两个分支产生的结果一样，
		但是使用变基的好处使得提交历史更加整洁，一个经过变基的分支的历史记录时会发现，尽管实际的开发工作是并行的，但它们看上去就像是串行的一样，
		提交历史是一条直线没有分叉。目的是为了确保在向远程分支推送时能保持提交历史的整洁

## 辅助命令

1. 用来统计git的提交历史

```git
git log --pretty=tformat: --numstat | awk '{ add += $1; subs += $2; loc += $1 - $2 } END { printf "added lines: %s, removed lines: %s, total lines: %s\n", add, subs, loc }'
```

- 返回格式为：
> added lines: 38210, removed lines: 12091, total lines: 26119



## 基础知识

1. git仓库的基本概念

![1614350547168](C:\Users\Helmet\AppData\Roaming\Typora\typora-user-images\1614350547168.png)



## 项目管理

