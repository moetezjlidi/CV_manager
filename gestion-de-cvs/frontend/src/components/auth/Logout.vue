<template>
  <h1>Logout</h1>
  <p v-if="message">{{ message }}</p>
</template>

<script setup>
import { onMounted, ref, inject } from "vue";

const user = inject("user");
const message = ref("");

onMounted(async () => {
  user.value.logout();
  message.value = "Vous êtes déconnecté.";

  setTimeout(() => {
    window.location.hash = "#/";
  }, 2000);
});
</script>


<style scoped>

</style>