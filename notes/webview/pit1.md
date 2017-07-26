记录一个 webview 的坑

在小米4 的 6.0 的系统上，当挂上代理的时候，会出现一个错误**net::ERR_SPDY_COMPRESSION_ERROR**.
将代理关掉即可。

- 原因
[据资料显示](https://stackoverflow.com/questions/23521839/failed-to-load-resource-neterr-content-length-mismatch)，
由于代理修改了报文的内容，但是没有更新头部中的'content-length'字段。目前更详细的原因还没查到。


