##### docker 安装

```
docker run --name srs -d -p 1935:1935 -p 1985:1985 -p 8080:8080 registry.cn-hangzhou.aliyuncs.com/ossrs/srs
```

##### 源码下载

```
git clone https://gitee.com/winlinvip/srs.oschina.git srs &&  git remote set-url origin https://github.com/ossrs/srs.git && git pull
```

##### 使用例子推流(doc目录下需要有对应的视频源文件)

```
for((;;)); do \
        ./objs/ffmpeg/bin/ffmpeg -re -i ./doc/source.200kbps.768x320.flv \
        -vcodec copy -acodec copy \
        -f flv -y rtmp://192.168.42.51/live/livestream; \
        sleep 1; \
    done
```

##### 配置文件在 conf目录下，默认使用srs.conf

##### 日志目录 ，默认在 `./objs/srs.log` 可在配置文件中修改

