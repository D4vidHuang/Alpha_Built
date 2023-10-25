<script setup>

import EcadiLogo from '../../assets/ecadi-logo.png';
import EcadiLogo_white from '../../assets/ECADI-white.png';
import {ref, provide, computed} from 'vue';

const colorTheme = ref('light');
provide('colorTheme', colorTheme);
const language = ref('en')
provide('language', language)

function toggleColorTheme() {
  colorTheme.value = colorTheme.value === 'light' ? 'dark' : 'light'
  console.log(colorTheme.value)
}

const logo = computed(() => colorTheme.value === 'dark' ? EcadiLogo : EcadiLogo_white);

</script>

<template>
  <div id="parent-container" class="parent-container"
       :class="{ 'light-main': colorTheme === 'light', 'dark-main': colorTheme === 'dark' }">
    <div id="column-container">
      <div id="column-one">
        <el-image alt="ECADI logo" :src=logo width="375" height="250"/>
      </div>
      <div id="column-two">
        <div style="margin: 10px;">
          <h3 id="greeting-title" class="title"
              :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }">
            Alpha Built
          </h3>
        </div>
        <div style="margin: 3%;">
          <h3 id="greeting" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
              v-if="language === 'en'">
            Discover the best online platform for architectural design.
            We offer a wide range of tools and features to help you
            create stunning designs for your architectural projects.
          </h3>
          <h3 id="greeting" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
              v-else-if="language === 'cn'">
            发现最佳的在线建筑设计平台。
            我们提供各种工具和功能，帮助您为您的建筑项目创建令人惊艳的设计。
          </h3>
          <h3 id="greeting" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
              v-else-if="language === 'kr'">
            건축 설계를 위한 최고의 온라인 플랫폼.
            우리는 훌륭한 디자인을 가진 건축 프로젝트를 위한 다양한 도구와 기능을 제공합니다.
          </h3>
          <h3 id="greeting" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
              v-else-if="language === 'nl'">
            Ontdek het beste online platform voor architecturaal ontwerp.
            We bieden allelei tools en functies om u te helpen met het ontwerpen voor uw architecturale projecten.
          </h3>
        </div>
      </div>
      <div id="column-three">
        <MainContent/>
      </div>
    </div>
    <select id="language-dropdown" class="button"
            :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
            name="language-selection" v-model="language">
      <option value="en">English</option>
      <option value="cn">中文</option>
      <option value="kr">한국어</option>
      <option value="nl">Nederlands</option>
    </select>
    <button id="theme-toggle-button" class="button"
            :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
            @click="toggleColorTheme" v-if="language === 'en'">Theme
    </button>
    <button id="theme-toggle-button" class="button"
            :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
            @click="toggleColorTheme" v-else-if="language === 'cn'">主题
    </button>
    <button id="theme-toggle-button" class="button"
            :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
            @click="toggleColorTheme" v-else-if="language === 'kr'">테마
    </button>
    <button id="theme-toggle-button" class="button"
            :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
            @click="toggleColorTheme" v-else-if="language === 'nl'">Thema
    </button>
  </div>
</template>

<style scoped>

:root {
  --light-color: #FFFFFF;
  --dark-color: #000000;
}

#theme-toggle-button {
  position: fixed;
  top: 20px;
  right: 20px;
}

#language-dropdown {
  position: fixed;
  top: 20px;
  right: 150px;
}

#column-container {
  width: 100%;
  height: 100%;
  overflow: hidden;
  display: flex;
}

#column-one {
  display: flex;
  padding: 5%;
  flex: 3;
  align-items: center;
  justify-content: center;
}

#column-two {
  display: flex;
  flex: 2;
  justify-content: center;
  flex-direction: column;
  padding: 0;
}

#column-three {
  flex: 5;
  display: flex;
  flex-direction: column;
  justify-content: center;
  left: 5%;
}

#greeting-title {
  font-size: 30px;
  margin-bottom: 0;
  text-align: left;
}

#greeting {
  margin-top: 0;
  text-align: left;
  font-size: 20px;
}

.parent-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
}

.light-main {
  background: #222222;
}

.dark-main {
  background: #FFFFFF;
}

.dark-word {
  color: #222222;
}

.light-word {
  color: #FFFFFF;
}

</style>
