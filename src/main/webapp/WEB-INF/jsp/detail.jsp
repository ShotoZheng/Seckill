<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="common/tag.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title>列表详情页面</title>
    <!--静态导入-->
    <%@include file="common/head.jsp" %>
</head>
<body>
<div class="container">
    <div class="panel panel-info text-center">
        <div class="panel-heading">
            <h2>${seckill.gname}</h2>
        </div>
        <div class="panel-body">
            <h2 class="text-danger">
                <!--显示Time图标-->
                <span class="glyphicon glyphicon-time"></span>
                <!--显示倒计时-->
                <span class="glyphicon" id="seckill-box"></span>
            </h2>
        </div>
    </div>
</div>
<!--登录弹出层，用于输入电话号码-->
<%--modal为bootstrap的一个组件，fade表示隐藏--%>
<div id="killPhoneModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <%----modal头部，文本显示信息--%>
            <div class="modal-header">
                <h3 class="modal-title text-center">
                    <span class="glyphicon glyphicon-phone"></span>秒杀电话：
                </h3>
            </div>
            <%--modal主体部分，填写对话框--%>
            <div class="modal-body">
                <div class="row">
                    <div class="col-xs-8 col-xs-offset-2">
                        <input type="text" name="killPhone" id="killPhoneKey"
                               placeholder="填写手机号" class="form-control"/>
                    </div>
                </div>
            </div>
            <%--出错信息显示和提交按钮--%>
            <div class="modal-footer">
                <!--验证信息-->
                <span id="killPhoneMessage" class="glyphicon"></span>
                <button type="button" id="killPhoneBtn" class="btn btn-success">
                    <span class="glyphicon glyphicon-phone"></span>Submit
                </button>
            </div>
        </div>
    </div>
</div>
</body>
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
<!--使用CDN 获取公共的js插件-->
<!--jquery cookie 操作插件-->
<script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<!--jquery countdown 倒计时插件-->
<script src="https://cdn.bootcss.com/jquery.countdown/2.2.0/jquery.countdown.min.js"></script>

<!--引入交互逻辑文件-->
<script type="text/javascript" src="/resources/script/seckill.js"></script>
<script type="text/javascript">
    $(function () {
        //使用EL表达式传入参数
        //调用详情页初始化方法
        seckill.detail.init({
            seckillId: ${seckill.seckillId},
            //startTime.time等价于long startTime.getTime();毫秒值方便js解析
            startTime: ${seckill.startTime.time},
            endTime: ${seckill.endTime.time}
        });
    });
</script>
</html>