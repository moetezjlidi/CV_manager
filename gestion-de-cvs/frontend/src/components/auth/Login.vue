<script setup>
import {ref, inject} from "vue";


const user = inject("user");
const name = ref("");
const password = ref("");
const message = ref("");


async function submitLogin() {
  let ok = await user.value.login(name.value, password.value);
  if (ok) {
    window.location.hash = "#/";
  } else {
    message.value = "bad credential";
  }
}

function abort() {
  window.location.hash = "#/";
}

</script>

<template>
  <h1>Authentication</h1>
  <div v-if="(message != '')" class="alert alert-warning">
    {{ message }}
  </div>

  <form id="app" method="post" novalidate="true">
    <div class="mb-3">
      <label>Login :</label>
      <input v-model="name" class="form-control"/>
    </div>
    <div class="mb-3">
      <label>Password :</label>
      <input v-model="password" class="form-control"/>
    </div>
    <div class="mb-3">
      <button @click.prevent="submitLogin()"
              class="ms-2 btn btn-primary">Login</button>
      <button @click.prevent="abort()"
              class="ms-2 btn btn-primary">Abort</button>
    </div>
  </form>
</template>

<style scoped>

</style>