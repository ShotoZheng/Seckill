var seckill = {
    //封装秒杀相关ajax的url
    URL: {
        now: function () {
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        execution: function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },

    //执行秒杀
    handleSeckillKill: function (seckillId, node) {
        //执行秒杀之前先隐藏包含有秒杀执行按钮的span
        node.hide().html('<button class="btn bg-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            //在回调函数中执行交互流程
            //success为SeckillResult的属性值
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {//true则表示开启秒杀
                    //获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log("killUrl:" + killUrl);
                    //绑定一次点击事件,防止重复点击秒杀
                    $('#killBtn').one('click', function () {
                        //执行秒杀请求
                        //1.禁用秒杀点击按钮
                        $(this).addClass('disabled');
                        //2.发送秒杀请求执行秒杀,即调用SeckillController的execute方法
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                //TODO 未使用到秒杀成功后返回的秒杀成功明细对象SuccessKilled
                                //3.将执行秒杀成功信息显示出来
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        });
                    });
                    node.show();
                } else {
                    //未开启秒杀，即当前PC机时间快于服务器时间的情况，需要重新计时
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    //重新计时
                    seckill.countdown(seckillId, now, start, end);
                }

            } else {
                console.log('result:' + result);
            }
        });
    },

    //验证手机号
    validatePhone: function (phone) {
        //phone不为空时为true，isNaN方法用于校验手机号是否为非数字，是的话为true
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },

    //时间倒计时操作
    countdown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        //时间判断
        if (nowTime > endTime) {
            //说明秒杀已经结束
            seckillBox.html('秒杀已经结束！');
        } else if (nowTime < startTime) {
            //秒杀还未开始，开始倒计时操作
            var killTime = new Date(startTime + 1000);//加1秒避免服务器时间偏移？
            seckillBox.countdown(killTime, function (event) {
                //时间格式
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown', function () {
                //到了执行秒杀时间
                //获取秒杀地址，控制显示逻辑，执行秒杀
                seckill.handleSeckillKill(seckillId, seckillBox);
            });
        } else {
            //开始秒杀
            seckill.handleSeckillKill(seckillId, seckillBox);
        }
    },

    //详情页秒杀逻辑
    detail: {
        //详情页初始化
        init: function (params) {
            //手机号验证和登录
            //在cookie 中查找手机号
            var killPhone = $.cookie('userPhone');
            //验证手机号
            if (!seckill.validatePhone(killPhone)) {//手机号符合规范
                //绑定手机号
                //获取弹出层
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭，即点击其他区域弹出层区域依旧显示
                    keyboard: false //关闭键盘事件，防止键盘点击而关闭弹出层
                });
                //获取弹出层的提交按钮的点击事件
                $('#killPhoneBtn').click(function () {
                    //获取对话框中输入的手机号
                    var inputPhone = $('#killPhoneKey').val();
                    //校验手机号码
                    if (seckill.validatePhone(inputPhone)) {
                        //校验成功将手机号写入cookie中
                        //expires: 7表示cookie有效期为7天，path表示在seckill模块（秒杀模块）下cookie才有效
                        $.cookie('userPhone', inputPhone, {expires: 7, path: '/seckill'});
                        //刷新页面，会再次调用init方法
                        window.location.reload();
                    } else {
                        //输入的手机号错误提示
                        //这里先把隐藏killPhoneMessage的span组件并添加进<label>标签，
                        //防止添加的过程在页面上呈现出来。show(300)即显示时间花费300毫秒（动态过程）
                        $('#killPhoneMessage').hide().html('<label ' +
                            'class="label label-danger">手机号不合法！</label>').show(300);
                    }
                });
            }//if
            //登录成功
            //获取传入的参数
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            //计时交互,get方法发送GET请求获取当前系统时间
            //result即SeckillResult的json数据，{true,nowTime}
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {//result存在且其succss属性为true
                    var nowTime = result['data'];
                    //时间判断,计时交互
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log('result=' + result);
                }
            });
        }
    }
}