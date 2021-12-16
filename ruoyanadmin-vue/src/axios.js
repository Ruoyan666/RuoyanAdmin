import axios from "axios";
import router from "./router";
import Element from "element-ui";

axios.defaults.baseURL = "http://localhost:8081";

const request = axios.create({
    timeout: 5000,
    headers:{
        'Content-Type': "application/json; charset=utf-8"
    }
})

request.interceptors.request.use(config =>{
    config.headers['Authorization'] = localStorage.getItem("token");

    return config;
} )

request.interceptors.response.use(response =>{

    console.log(response);

    let res = response.data;
    //返回的状态码为200，则代表操作成功，返回响应即可
    //若状态码不为200，则代表操作可能出现异常，则把异常信息输出
    //若为系统业务中出现异常，则从后端接受到状态码，判断异常类型，并做相应处理
    if (res.code === 200)
    {
        return response;
    }
    else
    {
        Element.Message.error(!res.msg ? '系统异常' : res.msg);
        return Promise.reject(response.data.msg);
    }
},error =>{

    console.log(error);

    if (error.response.data)
    {
        error.massage = error.response.data.msg;
    }

    if (error.response.status === 401)
    {
        router.push("/login");
    }

    Element.Message.error(error.massage,{duration:3000});

    return Promise.reject(error);

    }
)

export default request