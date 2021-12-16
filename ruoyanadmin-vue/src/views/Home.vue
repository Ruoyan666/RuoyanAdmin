<template>
  <el-container>
    <el-aside width="200px">

      <SideMenu></SideMenu>
    </el-aside>

    <el-container>
      <el-header>
        <strong>RuoyanAdmin后台管理系统</strong>

        <div class="header-avatar">
          <el-avatar size="medium" :src="userInfo.avatar"></el-avatar>

          <el-dropdown>
            <span class="el-dropdown-link">
              {{ userInfo.username }}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>
                <router-link :to="{name:'UserCenter'}" style="text-decoration: none">个人中心</router-link>
              </el-dropdown-item>
              <el-dropdown-item @click.native="logout">退出</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

          <el-link href="https://space.bilibili.com/11930596" target="_blank">网站</el-link>
          <el-link href="https://space.bilibili.com/11930596" target="_blank">B站</el-link>
        </div>
      </el-header>
      <el-main>
        <Tabs></Tabs>
        <div style="margin: 0 15px">
          <router-view/>
        </div>

      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import SideMenu from "./inc/SideMenu";
import Tabs from "./inc/Tabs"

export default {
  name: "Home",
  components: {

    SideMenu,
    Tabs
  },
  data(){
    return {
      userInfo:{
        id:"",
        username:"",
        avatar:"",
      }
    }
  },
  created() {
      this.getUserInfo();
  },
  methods:{
    getUserInfo(){
      this.$axios.get("/sys/userInfo").then(response => {
        this.userInfo = response.data.data;
      })
    },
    logout() {
      this.$axios.post("/logout").then(response => {
        //清空本地和session中的存储数据
        localStorage.clear();
        sessionStorage.clear();

        //调用resetState方法清空登录数据
        this.$store.commit("resetState");

        //跳转回登录页面
        this.$router.push("/login");
      })
    }
  }
}
</script>

<style scoped>

.el-container{
  padding: 0;
  margin: 0;
  height: 100%;
}

.header-avatar{
  float: right;
  width: 210px;
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
}

.el-header{
  background-color: #17B3A3;
  color: #333;
  text-align: center;
  line-height: 60px;
}

.el-aside {
  background-color: #D3DCE6;
  color: #333;
  line-height: 200px;
}

.el-main {
  color: #333;
  /*text-align: center;*/
  /*line-height: 160px;*/
  padding: 0;
}


</style>