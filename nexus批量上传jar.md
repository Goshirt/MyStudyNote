1.  进入nexus 管理控制台新建repo

![1609838596691](C:\Users\13672\AppData\Roaming\Typora\typora-user-images\1609838596691.png)



2. 选择maven2(hosted)![1609838673406](C:\Users\13672\AppData\Roaming\Typora\typora-user-images\1609838673406.png)
3. 设置repo 属性![1609838788741](C:\Users\13672\AppData\Roaming\Typora\typora-user-images\1609838788741.png)

4. 把需要上传到nexus的jar包打包传到centos任意文件夹并解压，例如/home/test 目录

5. 在/home/test 新建一个nexus-upload.sh脚本，脚本内容如下：

   ```
   #!/bin/bash
    
   # copy and run this script to the root of the repository directory containing files
   # this script attempts to exclude uploading itself explicitly so the script name is important
   # Get command line params
   while getopts ":r:u:p:" opt; do
   	case $opt in
   		r) REPO_URL="$OPTARG"
   		;;
   		u) USERNAME="$OPTARG"
   		;;
   		p) PASSWORD="$OPTARG"
   		;;
   	esac
   done
    
   find . -type f -not -path './mavenimport\.sh*' -not -path '*/\.*' -not -path '*/\^archetype\-catalog\.xml*' -not -path '*/\^maven\-metadata\-local*\.xml' -not -path '*/\^maven\-metadata\-deployment*\.xml' | sed "s|^\./||" | xargs -I '{}' curl -u "$USERNAME:$PASSWORD" -X PUT -v -T {} ${REPO_URL}/{} ;
   ```

6. 赋予脚本可执行权限

   ```
   chmod +x nexus-upload.sh
   ```

7. 获取刚刚新建的repo 的仓库地址![1609839147182](C:\Users\13672\AppData\Roaming\Typora\typora-user-images\1609839147182.png)

8. 使用脚本上传把jar包到repo 仓库。`-u` : 管理员账号     `-p `: 登录密码     `-r`: repo的url

   ```
   ./nexus-upload.sh -u 123456 -p 123456 -r http://xxxx/scprivate/repos/repository/EOSMS5/
   ```

   

   