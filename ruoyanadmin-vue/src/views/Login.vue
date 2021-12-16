<template>

  <el-row type="flex" class="row-bg" justify="center">
    <el-col :xl="6" :lg="7">
      <h2>欢迎来到RuoyanAdmin管理系统</h2>
      <el-image :src="require('@/assets/ruoyan.jpg')" style="height: 180px; width: 220px"></el-image>

      <p>学习开发 若言Ruoyan</p>
      <p>从前从前，有个人爱你很久，但偏偏，风渐渐，把距离吹的好远</p>
    </el-col>

    <el-col :span="1">
      <el-divider direction="vertical"></el-divider>
    </el-col>
    <el-col ::xl="6" :lg="7">
      <el-form :model="loginForm" :rules="rules" ref="loginForm" label-width="100px" class="demo-editForm" >
        <el-form-item label="用户名" prop="username" style="width: 380px">
          <el-input v-model="loginForm.username"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password" style="width: 380px">
          <el-input v-model="loginForm.password" type="password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="checked" style="float: left;margin-top: -15px;margin-bottom: -22px">记住我</el-checkbox>
        </el-form-item>
        <el-form-item label="验证码" prop="code" style="width: 380px" @keyup.enter.native="submitForm('loginForm')">
          <el-input v-model="loginForm.code" style="width: 152px;float: left"></el-input>
          <el-image class="captchaImage" :src="captchaImage" @click="getCaptchaImage"></el-image>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm('loginForm')">登录</el-button>
          <el-button @click="resetForm('loginForm')">重置</el-button>
        </el-form-item>
      </el-form>
    </el-col>
  </el-row>

</template>

<script>

  import qs from 'querystring'

export default {
  name: "login",
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        code: '',
        token: '',
        checked: false,
      },
      //输入框验证规则
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
        ],
        code: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
          { min: 5, max: 5, message: '长度为 5 个字符', trigger: 'blur' }
        ],
      },
      //验证码初始数据为null
      captchaImage: null
    }
  },
  methods: {
    //提交表单方法
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$axios.post('/login?' + qs.stringify(this.loginForm)).then(response =>{

              const jwt = response.headers['authorization'];

              this.$store.commit('SET_TOKEN',jwt);

              this.$router.push("/index");

          }).catch(error =>{
            this.getCaptchaImage();
          })
        } else {

            console.log('error submit!!');

            return false;
        }
      });
    },
    //重置表单数据方法
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    //拿到验证码数据方法
    getCaptchaImage(){
          this.$axios.get('/captcha').then(response =>{

              console.log(response);
              console.log("/captcha");
              this.loginForm.token = response.data.data.token;
              this.captchaImage = response.data.data.captchaImage;
              this.loginForm.code = '';
          })

    }
  },
  created(){
    this.getCaptchaImage();
  }
}
</script>

<style scoped>

  .el-row{
    background-color: #fafafa;
    height: 100%;
    display: flex;
    align-items: center;
    text-align: center;
  }

  .el-divider{
    height: 200px;
  }

  .captchaImage{
    float: left;
    margin-left: 8px;
    border-radius: 4px;
  }

</style>