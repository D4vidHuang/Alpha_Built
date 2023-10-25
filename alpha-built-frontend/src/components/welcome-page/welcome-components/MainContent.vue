<script setup>

import WelcomeItem from './WelcomeItem.vue'
import DocumentationIcon from '../icons/IconDocumentation.vue'
import ToolingIcon from '../icons/IconTooling.vue'
import EcosystemIcon from '../icons/IconEcosystem.vue'
import CommunityIcon from '../icons/IconCommunity.vue'
import SupportIcon from '../icons/IconSupport.vue'
import router from "@/router";
import {inject} from 'vue';

const colorTheme = inject('colorTheme');
const language = inject('language');

</script>

<template>
  <WelcomeItem>
    <template #icon>
      <DocumentationIcon/>
    </template>
    <template #heading>
      <button class="button" :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
              v-if="language === 'en'"
              @click="$router.push('/auth/sign-up')">Register
      </button>
      <button class="button" :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
              v-else-if="language === 'cn'"
              @click="$router.push('/auth/sign-up')">注册
      </button>
      <button class="button" :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
              v-else-if="language === 'kr'"
              @click="$router.push('/auth/sign-up')">등록
      </button>
      <button class="button" :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
              v-else-if="language === 'nl'"
              @click="$router.push('/auth/sign-up')">Registreren
      </button>
    </template>
    <br/>
    <div class="word" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-if="language === 'en'">Want to try our new platform? Register here!
    </div>
    <div class="word" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'cn' ">你想要尝试我们新平台吗？在这里注册！
    </div>
    <div class="word" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'kr' ">새로운 플랫폼을 사용해 보시겠습니까? 여기서 등록하세요！
    </div>
    <div class="word" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'nl' ">Wil je onze nieuwe platform uitproberen? Registreer hier!
    </div>
    <br/>
  </WelcomeItem>

  <WelcomeItem>
    <template #icon>
      <ToolingIcon/>
    </template>
    <template #heading>
      <button class="button" :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
              v-if="language === 'en'"
              @click="$router.push('/auth/log-in')">Login
      </button>
      <button class="button" :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
              v-else-if="language === 'cn'"
              @click="$router.push('/auth/log-in')">登陆
      </button>
      <button class="button" :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
              v-else-if="language === 'kr'"
              @click="$router.push('/auth/log-in')">로그인
      </button>
      <button class="button" :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
              v-else-if="language === 'nl'"
              @click="$router.push('/auth/log-in')">Inloggen
      </button>
    </template>
    <br/>
    <div class="word" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-if="language === 'en'">Already our account? Login here!<br/></div>
    <div class="word" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'cn'">已有账号？在这里登陆！<br/></div>
    <div class="word" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'kr'">이미 계정이 있습니까? 여기서 로그인하세요!<br/></div>
    <div class="word" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'nl'">Heb je al een account? Log in hier!<br/></div>

  </WelcomeItem>

  <WelcomeItem>
    <template #icon>
      <EcosystemIcon/>
    </template>
    <template #heading>
      <button class="button" :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
              v-if="language === 'en'"
              @click="router.push('/design-page')">Quick Start
      </button>
      <button class="button" :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
              v-else-if="language === 'cn'"
              @click="router.push('/design-page')">快速开始
      </button>
      <button class="button" :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
              v-else-if="language === 'kr'"
              @click="router.push('/design-page')">빨리 시작
      </button>
      <button class="button" :class="{ 'light-theme': colorTheme === 'light', 'dark-theme': colorTheme === 'dark' }"
              v-else-if="language === 'nl'"
              @click="router.push('/design-page')">Uitproberen
      </button>
    </template>
  </WelcomeItem>

  <WelcomeItem>
    <template #icon>
      <CommunityIcon/>
    </template>
    <div class="title" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-if="language === 'en'">
      Documentation
    </div>
    <div class="title" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'cn'">
      文档
    </div>
    <div class="title" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'kr'">
      문서
    </div>
    <div class="title" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'nl'">
      Documentatie
    </div>
    <div class="word" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-if="language === 'en'">Got stuck? Please find our official documentation <a href="https://www.google.com"
                                                                                       target="_blank" rel="noopener">here</a>.
    </div>
    <div class="word" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'cn'">遇到问题? 在<a href="https://www.google.com" target="_blank"
                                                      rel="noopener">这里</a>查看文档。
    </div>
    <div class="word" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'kr'">이슈가 있으신가요? <a href="https://www.google.com" target="_blank" rel="noopener">여기</a>서
      공식 문서를 확인할수 있습니다.
    </div>
    <div class="word" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'nl'">Heb je vragen? <a href="https://www.google.com" target="_blank" rel="noopener">hier</a> vind je
      onze officiële documentatie.
    </div>

  </WelcomeItem>

  <WelcomeItem>
    <template #icon>
      <SupportIcon/>
    </template>
    <div class="title" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-if="language === 'en'">Support Us
    </div>
    <div class="title" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'cn'">支持我们
    </div>
    <div class="title" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'kr'">서포트하기
    </div>
    <div class="title" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'nl'">Steun ons
    </div>
    <div class="word" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-if="language === 'en'">Want to support us? Please visit our official website at
      <a href="https://www.ecadi.com" target="_blank" rel="noopener">ECADI</a>.
    </div>
    <div class="word" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'cn'">想要支持我们? 我们的官方网站在这里<a href="https://www.ecadi.com" target="_blank"
                                                                            rel="noopener">ECADI</a>.
    </div>
    <div class="word" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'kr'">저희를 서포트해 주시려면 <a href="https://www.ecadi.com" target="_blank" rel="noopener">ECADI</a>
      공식 웹사이트를 방문해주시기 바랍니다.
    </div>
    <div class="word" :class="{ 'light-word': colorTheme === 'light', 'dark-word': colorTheme === 'dark' }"
         v-else-if="language === 'nl'">Wil je ons ondersteunen? Bezoek onze officiele website
      <a href="https://www.ecadi.com" target="_blank" rel="noopener">ECADI</a>
    </div>
  </WelcomeItem>
</template>

<script>

</script>

<style>

.title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.light-word {
  color: #FFFFFF;
}

.dark-word {
  color: #222222;
}

.dark-theme {
  background-color: hsl(0, 100%, 40%);
  color: hsl(0, 0%, 100%);
}

.dark-theme:hover {
  background-color: hsl(352, 96%, 49%);
}

.light-theme {
  background-color: hsl(120, 100%, 25%);
  color: hsl(0, 0%, 100%);
}

.light-theme:hover {
  background-color: hsl(120, 100%, 35%);
}

.button {
  display: inline-block;
  padding: 0.5rem 1rem;
  border-radius: 0.25rem;
//background-color: hsl(0, 100%, 40%); //color: hsl(0, 0%, 100%); transition: 0.4s; font-weight: bolder; font-size: larger;
}


@media (hover: hover) {
  a:hover {
    background-color: hsla(160, 100%, 37%, 0.2);
  }
}

:root {
  --vt-c-white: #ffffff;
  --vt-c-white-soft: #f8f8f8;
  --vt-c-white-mute: #f2f2f2;

  --vt-c-black: #181818;
  --vt-c-black-soft: #222222;
  --vt-c-black-mute: #282828;

  --vt-c-indigo: #2c3e50;

  --vt-c-divider-light-1: rgba(60, 60, 60, 0.29);
  --vt-c-divider-light-2: rgba(60, 60, 60, 0.12);
  --vt-c-divider-dark-1: rgba(84, 84, 84, 0.65);
  --vt-c-divider-dark-2: rgba(84, 84, 84, 0.48);

  --vt-c-text-light-1: var(--vt-c-indigo);
  --vt-c-text-light-2: rgba(60, 60, 60, 0.66);
  --vt-c-text-dark-1: var(--vt-c-white);
  --vt-c-text-dark-2: rgba(235, 235, 235, 0.64);
}

/* semantic color variables for this project */
:root {
  --color-background: var(--vt-c-white);
  --color-background-soft: var(--vt-c-white-soft);
  --color-background-mute: var(--vt-c-white-mute);

  --color-border: var(--vt-c-divider-light-2);
  --color-border-hover: var(--vt-c-divider-light-1);

  --color-heading: var(--vt-c-text-light-1);
  --color-text: var(--vt-c-text-light-1);

  --section-gap: 160px;
}

@media (prefers-color-scheme: dark) {
  :root {
    --color-background: var(--vt-c-black);
    --color-background-soft: var(--vt-c-black-soft);
    --color-background-mute: var(--vt-c-black-mute);

    --color-border: var(--vt-c-divider-dark-2);
    --color-border-hover: var(--vt-c-divider-dark-1);

    --color-heading: var(--vt-c-text-dark-1);
    --color-text: var(--vt-c-text-dark-2);
  }
}

*,
*::before,
*::after {
  box-sizing: border-box;
  margin: 0;
  position: relative;
  font-weight: normal;
}

body {
  min-height: 100vh;
  color: var(--color-text);
  background: var(--color-background);
  transition: color 0.5s, background-color 0.5s;
  line-height: 1.6;
  font-family: Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu,
  Cantarell, 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
  font-size: 15px;
  text-rendering: optimizeLegibility;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

</style>