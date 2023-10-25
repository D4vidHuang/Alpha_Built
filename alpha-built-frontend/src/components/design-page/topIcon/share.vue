<template>
  <el-button class="share" @click="copyLink">
    share
  </el-button>
</template>

<script>
export default {
  name: 'Share',
  methods: {
    copyLink() {
      if (navigator.clipboard) {
        navigator.clipboard.writeText(window.location.href)
            .then(() => {
              alert('Link copied to clipboard');
            })
            .catch(err => {
              console.error('Could not copy text: ', err);
            });
      } else {
        let textarea = document.createElement("textarea");
        textarea.textContent = window.location.href;
        textarea.style.position = "fixed";
        document.body.appendChild(textarea);
        textarea.select();
        try {
          document.execCommand("copy");
          alert('Link copied to clipboard');
        } catch (ex) {
          console.warn("Copy to clipboard failed.", ex);
          return false;
        } finally {
          document.body.removeChild(textarea);
        }
      }
    }
  }
}
</script>

<style scoped>
.share {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 0px 25px;
  isolation: isolate;
  width: 100px;
  height: 29px;
  background: #62D84E;
  box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
  border-radius: 10px;
  font-family: 'Inter';
  font-style: normal;
  font-weight: 700;
  font-size: 24px;
  line-height: 29px;
}
</style>