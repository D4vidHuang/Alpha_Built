<template>
  <div class="version">
        <!--<img class="vector" src="src/assets/layers.svg" alt="version"/>-->
        <!--<h3> Versions </h3>-->
    <div class="subversion" id="subversion-up">
      <div class="rightBarButton" id="right-bar-button-1"></div>
      <div class="rightBarButton" id="right-bar-button-2"></div>
      <div class="rightBarButton" id="right-bar-button-3"></div>
    </div>
    <div class="subversion" id="subversion-down">
      <div class="rightBarContent" id="right-bar-content-1"></div>
      <div class="rightBarContent" id="right-bar-content-2"></div>
      <div class="rightBarContent" id="right-bar-content-3"></div>
    </div>
  </div>
</template>


<script>

export default {
    name: "version",
  mounted() {
    const rightBarButton1 = document.getElementById("right-bar-button-1")
    const rightBarButton2 = document.getElementById("right-bar-button-2")
    const rightBarButton3 = document.getElementById("right-bar-button-3")
    const rightBarContent1 = document.getElementById("right-bar-content-1")
    const rightBarContent2 = document.getElementById("right-bar-content-2")
    const rightBarContent3 = document.getElementById("right-bar-content-3")

    rightBarButton1.addEventListener("click", () => {
      rightBarContent1.style.display = "block"
      rightBarContent2.style.display = "none"
      rightBarContent3.style.display = "none"
    })

    rightBarButton2.addEventListener("click", () => {
      rightBarContent2.style.display = "block"
      rightBarContent1.style.display = "none"
      rightBarContent3.style.display = "none"
    })

    rightBarButton3.addEventListener("click", () => {
      rightBarContent3.style.display = "block"
      rightBarContent1.style.display = "none"
      rightBarContent2.style.display = "none"
    })
  }

}
</script>

<style scoped>
.version {
  position: absolute;
  right: 0;
  top: 8%;
  background: #FFFFFF;
  border-radius: 10px 0px 0px 10px;
  padding: 20px 15px 80px;
  width: 120px;
  height: 524px;
  box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);

  display: flex;
  flex-direction: column;
}

.subversion {
  display: flex;
  flex-direction: row;
}

#subversion-up {
  flex: 0.2;
  background-color: limegreen;
}

#subversion-down {
  flex: 0.8;
  background-color: lightseagreen;;
}

.rightBarButton {
  flex: 0.33;
}


#right-bar-button-1 {
  background-color: darkred;
}

#right-bar-button-2 {
  background-color: yellowgreen;
}

#right-bar-button-3 {
  background-color: deepskyblue;
}

.rightBarContent {
  position: relative;
  width: 100%;
  height: 100%;
  display: none;
}

#right-bar-content-1 {
  background-color: darkred;
}

#right-bar-content-2 {
  background-color: yellowgreen;
}

#right-bar-content-3 {
  background-color: deepskyblue;
}


</style>