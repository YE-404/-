<%--
  Created by IntelliJ IDEA.
  User: sy
  Date: 2021/11/30
  Time: 下午12:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>

<head>

    <title>Login</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" charset="utf-8" />
    <!--script type="text/javascript" src="js/jquery.js"></script-->
    <script type="application/x-javascript">
        addEventListener("load", function() { setTimeout(hideURLbar, 0); }, false); function hideURLbar(){ window.scrollTo(0,1); }
    </script>
    <meta
            name="keywords"
            content="Flat Dark Web Login Form Responsive Templates, Iphone Widget Template, Smartphone login forms,Login form, Widget Template, Responsive Templates, a Ipad 404 Templates, Flat Responsive Templates"
    />
    <link href="http://localhost:8080/mydemo_war/css/login/style.css" rel="stylesheet" type="text/css" />



</head>
<body>


<h1>机房识别系统</h1>
<div class="login-form">
    <div class="close"></div>
    <div class="head-info">
        <label class="lbl-1"> </label>
        <label class="lbl-2"> </label>
        <label class="lbl-3"> </label>
    </div>
    <div class="clear"></div>
    <div class="avtar">
        <img src ="http://localhost:8080/mydemo_war/img/avtar.png"alt ="用户图片"/>
    </div>
    <div id="app">
        <form action="/mydemo_war/user/login" method='POST'>
            <input
                    type="text"
                    class="text"
                    name = "username"
                    value="admin"
                    v-model="user.username"
                    onfocus="this.value = '';"
                    onblur="if (this.value == '') {this.value = 'Username';}"
            />
            <div class="key">
                <input
                        type="password"
                        name = "password"
                        value="123456"
                        v-model="user.password"
                        onfocus="this.value = '';"
                        onblur="if (this.value == '') {this.value = 'Password';}"
                />
            </div>
            <font color="red">{{message}}<font/>

                <br/>
                <div class="signin">
                    <input type="button" value="Login" @click="submit" />
                    <a href="/mydemo_war/user/registercover">没有账号？点击注册</a>
                </div>
        </form>
    </div>
</div>

<!--div class="copy-rights">
  <p>
    Copyright &copy; 2015.Company name All rights reserved.<a
      target="_blank"
      href="http://sc.chinaz.com/moban/"
      >&#x7F51;&#x9875;&#x6A21;&#x677F;</a
    >
  </p>
</div-->


<script src="http://localhost:8080/mydemo_war/js/axios-0.18.0.js" ></script>
<script src="http://localhost:8080/mydemo_war/js/vue.js"></script>
<script>
    new Vue({
        el:"#app",
        data(){
            return{
                message:"",
                user:{}
            }
        },
        methods:{
            submit(){
                var _this = this;
                axios({
                    method:"post",
                    url: "http://localhost:8080/mydemo_war/user/logincheck",
                    data: _this.user
                }).then(function (resp){
                    _this.message = resp.data;
                    if(_this.message == "ok"){
                        _this.message = "登录成功";
                        window.location.href="http://localhost:8080/mydemo_war/emptyservlet";
                    }else if(_this.message == "wrong"){
                        _this.message = "账号或密码错误";
                        console.log(_this.message);
                    }



                })
            }
        }
    })
</script>
</body>
</html>
