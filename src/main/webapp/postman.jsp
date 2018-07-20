<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <style>
        * {
            margin: 0;
            padding: 0;
        }

        body {
            font: 12px "lucida Grande",Verdana,"Microsoft YaHei";
        }

        input.inputstyle {
            border-color: transparent transparent #555 transparent;
            line-height: 30px;
            outline: none;
            min-width: 400px;
        }

        textarea {
            border-radius: 4px;
            outline: none;
            width: 600px;
            min-height: 200px;
        }

        ul li {
            list-style: none;
            float: left;
        }

        ul li a {
            display: inline-block;
            text-decoration: none;
            padding: 5px 10px;
        }

        ul li label {
            color: #96a5b4;
            /** border-left: 1px solid #96a5b4; **/
        }

        header {
            background: #F9FCFE;
            opacity:0.8;
            padding: 12px 36px;
        }

        header section.logo {
            float: left;
            overflow: hidden;
        }

        header nav {
            float: right;
        }

        .contentwrapper {
            min-height: 400px;
            margin: 50px auto;
            padding: 0 24px;
        }

        .content {
            position: relative;
        }

        .history {
            position: absolute;
            left: 0px;
            top: 0px;
            width: 150px;
            border: 1px solid;
        }

        .history ul li {
            display: block;
            width: 100%;
            position: relative;
        }

        .history ul li a {
            display: block;
        }

        .history ul li:hover {
            background: #6f95c8;
        }

        .history ul li div.keepArea {
            display: none;
            width: 40px;
            height: 20px;
            border: 1px solid #fa9fde;
            position: absolute;
            left: 100%;
            top: 2%;
        }

        .history ul li div.keepArea a {
            line-height: 16px;
            display: block;
            padding: 2px 7px;
        }

        .history ul li:hover > div.keepArea {
            display: block;
        }

        .postarea {
            min-width: 600px;
            max-width: 900px;
            margin: 0 auto;
            border: 1px solid #f10000;
        }

        .keepList {
            position: absolute;
            right: 0px;
            top: 0px;
            width: 150px;
            border: 1px solid;
        }

        .keepList ul li {
            display: block;
            width: 100%;
            position: relative;
        }

        .keepList ul li a {
            display: block;
        }

        .keepList ul li:hover {
            background: #6f95c8;
        }

        .theme {
            height: 50px;
            line-height: 50px;
            border: 1px solid #c2c2c2;
            font-size: 16px;
            background: #F9FCFE;
            opacity:0.8;
            text-align: center;
        }

        .content nav li {
            clear: both;
        }

        .content section {
            padding:  10px 40px;
            font-family: Verdana,Tahoma, Arail;
            font-size: 16px;
            color: #333;
        }

        footer {
            position: absolute;
            width: 100%;
            bottom: 0;
            padding: 12px 0;
            text-align: center;
            background: #F9FCFE;
            opacity:0.8;
        }

        footer nav {
            display: inline-block;
        }

    </style>
    <meta charset="utf-8">
    <script type="text/javascript">
        function RequestMsg(description, reqUrl, reqData, respData) {
            this.description = description;
            this.reqUrl = reqUrl;
            this.reqData = reqData;
            this.respData = respData;
            this.status = '0';

            RequestMsg.prototype.getDescription = function() {return this.description; };
            RequestMsg.prototype.getReqUrl = function() {return this.reqUrl; };
            RequestMsg.prototype.getReqData = function() {return this.reqData; };
            RequestMsg.prototype.getRespData = function() {return this.respData; };
            RequestMsg.prototype.getStatus = function() {return this.status; };
            RequestMsg.prototype.setStatus = function() {return this.status = status; };
        }

        function postRequest() {
            var reqUrl = document.getElementsByName("reqUrl")[0].value;
            var reqData = document.getElementsByName("reqData")[0].value;

            var xhr = new XMLHttpRequest();
            xhr.open("post", reqUrl, true);
            xhr.setRequestHeader("Content-Type", "text/plain;charset=utf-8");
            // 新增期望返回数据类型, 解决乱码
            xhr.setRequestHeader("Accept", "text/plain;charset=utf-8");
            xhr.send(reqData);

            xhr.onreadystatechange = function() {
                if(xhr.readyState == 4) {
                    if((xhr.status >= 200 && xhr.status < 300) || xhr.status == 304 || xhr.status == 0) {
                        document.getElementsByName("respData")[0].value = xhr.responseText;
                    } else {
                        document.getElementsByName("respData")[0].value = "Request was unsuccessful: " + xhr.status;
                    }

                    // 添加到本地缓存当中
                    if(document.getElementsByName("reqDescription")[0].value != "undefined") {
                        // 1. 请求数据存放到历史记录中
                        var reqMsg = new RequestMsg(document.getElementsByName("reqDescription")[0].value, document.getElementsByName("reqUrl")[0].value,
                                document.getElementsByName("reqData")[0].value, document.getElementsByName("respData")[0].value);

                        var a = document.createElement("a");
                        a.onclick = clickDisplay;
                        a.href = "#";
                        a.innerHTML = reqMsg.getDescription();

                        var li = document.createElement("li");
                        li.appendChild(a);

                        document.getElementById("historyUl").appendChild(li);

                        // 2. 请求数据放入到本地存储中
                        var reqMsgs = getLocalStorage().getItem("reqMsgs");
                        if(reqMsgs == null || reqMsgs == "undefined") {
                            reqMsgs = "[]";
                        }
                        reqMsgs = JSON.parse(reqMsgs);


                        var flag = false;
                        for(var i in reqMsgs) {
                            if(reqMsgs[i].description === reqMsg.getDescription()) {
                                alert("之前有对该描述进行本地缓存过,缓存将被更新");
                                reqMsgs[i] = reqMsg;
                                flag = true;
                                break;
                            }
                        }
                        if(flag == false) {
                            reqMsgs[reqMsgs.length] = reqMsg;
                        }

                        getLocalStorage().setItem("reqMsgs", JSON.stringify(reqMsgs));
                    }
                }
            }

        }

        function clickDisplay() {
            var reqMsgs = JSON.parse(getLocalStorage().getItem("reqMsgs"));
            for(var i in reqMsgs) {
                if(reqMsgs[i].description === this.innerHTML) {
                    document.getElementsByName("reqUrl")[0].value = reqMsgs[i].reqUrl;
                    document.getElementsByName("reqData")[0].value = reqMsgs[i].reqData;
                    document.getElementsByName("reqDescription")[0].value = reqMsgs[i].description;
                    break;
                }
            }

        }

        window.onload = function() {
            // 从本地存储获取数据存放到历史记录中
            var reqMsgs = getLocalStorage().getItem("reqMsgs");
            if(reqMsgs == null || reqMsgs == "undefined") {
                reqMsgs = "[]";
            }
            reqMsgs = JSON.parse(reqMsgs);
            for(var i in reqMsgs) {
                if(reqMsgs[i].description != "" && reqMsgs[i].description != null) {
                    // 1. 创建li元素
                    var a = document.createElement("a");
                    a.onclick = clickDisplay;
                    a.href = "#";
                    a.innerHTML = reqMsgs[i].description;

                    var li = document.createElement("li");
                    li.appendChild(a);

                    // 2. 添加到对应节点中
                    if(reqMsgs[i].status === '1') {
                        document.getElementById("keepUl").appendChild(li);
                    }

                    if (reqMsgs[i].status == null || reqMsgs[i].status === '0') {
                        var keep = document.createElement("a");
                        keep.onclick = keepReq;
                        keep.href = "#";
                        keep.innerHTML = '收藏';
                        keep.alt = reqMsgs[i].description;

                        var aside = document.createElement("div");
                        aside.display = 'none';
                        aside.className = 'keepArea';
                        aside.appendChild(keep);

                        li.appendChild(aside);

                        document.getElementById("historyUl").appendChild(li);
                    }
                }
            }
        };

        function keepReq() {
            alert('收藏 [' + this.alt + ']成功！');
            // 从本地存储获取数据存放到历史记录中
            var reqMsgs = getLocalStorage().getItem("reqMsgs");
            if(reqMsgs == null || reqMsgs == "undefined") {
                reqMsgs = "[]";
            }
            reqMsgs = JSON.parse(reqMsgs);
            for(var i in reqMsgs) {
                if(reqMsgs[i].description != "" && reqMsgs[i].description != null && reqMsgs[i].description === this.alt) {
                    // 1. 往收藏列表添加元素
                    var a = document.createElement("a");
                    a.onclick = clickDisplay;
                    a.href = "#";
                    a.innerHTML = reqMsgs[i].description;

                    var li = document.createElement("li");
                    li.appendChild(a);

                    document.getElementById("keepUl").appendChild(li);

                    // 2. 修改本地数据
                    reqMsgs[i].status = '1';
                    break;
                }
            }
            getLocalStorage().setItem("reqMsgs", JSON.stringify(reqMsgs));
        }

        /*
         * 格式化请求JSON数据
         */
        function formatReqData() {
            var source = document.getElementsByName("reqData")[0].value;
            formatData(document.getElementsByName("reqData")[0], JSON.stringify(JSON.parse(source), null, 4));
        }

        /*
         * 格式化响应JSON数据
         */
        function formatRespData() {
            var source = document.getElementsByName("respData")[0].value;
            formatData(document.getElementsByName("respData")[0], JSON.stringify(JSON.parse(source), null, 4))
        }

        function formatData(dom, formatData) {
            dom.value = formatData;
        }
    </script>
</head>
<body>
    <div class="contentwrapper">
        <div class="content">
            <div class="history">
                <h1 class="theme">历史记录</h1>
                <nav>
                    <ul id="historyUl">

                    </ul>
                </nav>
            </div>
            <div class="postarea">
                <h1 class="theme">请求调用</h1>

                <section>
                    <label>请求描述： </label>
                    <input type="text" class="inputstyle" name="reqDescription" alt="请求描述" placeholder="请输入该请求描述" />
                </section>

                <section>
                    <label>请求URL： </label>
                    <input type="text" class="inputstyle" name="reqUrl" alt="请求url" placeholder="请输入该请求url" />
                </section>
                <section class="reqDataArea">
                    <h1>请求发送数据： </h1>
                    <textarea class="reqData" name="reqData"></textarea>
                    <input type="button" value="格式化JSON数据" onclick="formatReqData()" />
                </section>
                <section>
                    <input type="button" onclick="postRequest()" value="发送请求" />
                </section>

                <section class="respDataArea">
                    <h1>服务端响应数据： </h1>
                    <textarea class="respData" name="respData"></textarea>
                    <input type="button" value="格式化JSON数据" onclick="formatRespData()" />
                </section>
            </div>

            <aside class="keepList">
            <h1 class="theme">收藏列表</h1>
            <nav>
                <ul id="keepUl">

                </ul>
            </nav>
        </aside>
        </div>
    </div>
</body>
</html>
