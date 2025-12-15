<template>
  <div class="invitation-page">
    <div class="container-fluid">
      <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
          <div class="card shadow-lg mt-5">
            <div class="card-body p-5">
              <div v-if="loading" class="text-center">
                <div class="spinner-border text-primary mb-3" role="status">
                  <span class="visually-hidden">Chargement...</span>
                </div>
                <p>Vérification de l'invitation...</p>
              </div>

              <div v-else-if="error" class="text-center">
                <div class="alert alert-danger">
                  <i class="bi bi-x-circle-fill"></i>
                  <h4>Invitation invalide</h4>
                  <p>{{ error }}</p>
                </div>
                <a href="#/" class="btn btn-secondary">
                  <i class="bi bi-house"></i> Retour à l'accueil
                </a>
              </div>

              <div v-else-if="success" class="text-center">
                <div class="alert alert-success">
                  <i class="bi bi-check-circle-fill"></i>
                  <h4>Compte créé avec succès !</h4>
                  <p>Vous pouvez maintenant vous connecter avec vos identifiants.</p>
                </div>
                <a href="#/login" class="btn btn-primary">
                  <i class="bi bi-box-arrow-in-right"></i> Se connecter
                </a>
              </div>

              <form v-else @submit.prevent="handleSubmit">
                <div class="text-center mb-4">
                  <i class="bi bi-person-check text-primary" style="font-size: 3rem;"></i>
                  <h2 class="mt-3">Créer votre compte</h2>
                  <p class="text-muted">
                    Vous avez été invité(e) à rejoindre notre plateforme de CVs
                  </p>
                </div>

                <div class="invitation-info bg-light p-3 rounded mb-4">
                  <h5>Informations de l'invitation :</h5>
                  <p><strong>Nom :</strong> {{ invitation.firstName }} {{ invitation.lastName }}</p>
                  <p><strong>Email :</strong> {{ invitation.email }}</p>
                  <p v-if="invitation.website"><strong>Site web :</strong> {{ invitation.website }}</p>
                </div>

                <div class="mb-3">
                  <label for="username" class="form-label">Nom d'utilisateur <span class="text-danger">*</span></label>
                  <input 
                    id="username"
                    v-model="form.username" 
                    type="text"
                    class="form-control" 
                    :class="{'is-invalid': errors.username}"
                    placeholder="Choisissez un nom d'utilisateur"
                    required
                  />
                  <div v-if="errors.username" class="invalid-feedback">
                    {{ errors.username }}
                  </div>
                </div>

                <div class="mb-3">
                  <label for="password" class="form-label">Mot de passe <span class="text-danger">*</span></label>
                  <div class="input-group">
                    <input 
                      id="password"
                      v-model="form.password" 
                      :type="showPassword ? 'text' : 'password'"
                      class="form-control" 
                      :class="{'is-invalid': errors.password}"
                      placeholder="Choisissez un mot de passe"
                      required
                    />
                    <button 
                      class="btn btn-outline-secondary" 
                      type="button"
                      @click="showPassword = !showPassword"
                    >
                      <i :class="showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
                    </button>
                  </div>
                  <div v-if="errors.password" class="invalid-feedback d-block">
                    {{ errors.password }}
                  </div>
                  <small class="form-text text-muted">
                    Au moins 6 caractères
                  </small>
                </div>

                <div class="mb-3">
                  <label for="confirmPassword" class="form-label">Confirmer le mot de passe <span class="text-danger">*</span></label>
                  <input 
                    id="confirmPassword"
                    v-model="form.confirmPassword" 
                    type="password"
                    class="form-control" 
                    :class="{'is-invalid': errors.confirmPassword}"
                    placeholder="Confirmez votre mot de passe"
                    required
                  />
                  <div v-if="errors.confirmPassword" class="invalid-feedback">
                    {{ errors.confirmPassword }}
                  </div>
                </div>

                <div v-if="generalError" class="alert alert-danger">
                  <i class="bi bi-x-circle"></i> {{ generalError }}
                </div>

                <button type="submit" class="btn btn-primary w-100" :disabled="submitting">
                  <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
                  <i v-else class="bi bi-person-plus"></i>
                  Créer mon compte
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      invitation: null,
      loading: true,
      error: null,
      success: false,
      form: {
        username: '',
        password: '',
        confirmPassword: ''
      },
      errors: {},
      generalError: null,
      submitting: false,
      showPassword: false
    };
  },  mounted() {
    this.loadInvitation();
  },
  computed: {
    $route() {
      const path = window.location.hash.slice(1) || '/';
      const parts = path.split('/');
      return {
        params: {
          token: parts[2] // /invitation/:token
        }
      };
    }
  },
  methods: {
    loadInvitation() {
      const token = this.$route.params.token;
      
      axios.get(`/api/invitations/${token}`)
        .then(response => {
          this.invitation = response.data;
          this.loading = false;
        })
        .catch(err => {
          console.error('Erreur:', err);
          this.loading = false;
          if (err.response && err.response.status === 404) {
            this.error = 'Cette invitation est invalide ou a expiré.';
          } else {
            this.error = 'Erreur lors de la vérification de l\'invitation.';
          }
        });
    },
    
    validateForm() {
      this.errors = {};
      
      if (!this.form.username || this.form.username.trim().length < 3) {
        this.errors.username = 'Le nom d\'utilisateur doit contenir au moins 3 caractères';
      }
      
      if (!this.form.password || this.form.password.length < 6) {
        this.errors.password = 'Le mot de passe doit contenir au moins 6 caractères';
      }
      
      if (this.form.password !== this.form.confirmPassword) {
        this.errors.confirmPassword = 'Les mots de passe ne correspondent pas';
      }
      
      return Object.keys(this.errors).length === 0;
    },
    
    handleSubmit() {
      this.generalError = null;
      
      if (!this.validateForm()) {
        return;
      }
      
      this.submitting = true;
      
      const userData = {
        username: this.form.username.trim(),
        password: this.form.password,
        email: this.invitation.email,
        invitationToken: this.invitation.token
      };
        // TODO: Implement the signup endpoint that accepts invitation tokens
      axios.post('/secu-users/signup-with-invitation', userData)
        .then(response => {
          this.success = true;
          this.submitting = false;
        })
        .catch(err => {
          console.error('Erreur:', err);
          this.submitting = false;
          
          if (err.response) {
            if (err.response.status === 409) {
              this.generalError = 'Ce nom d\'utilisateur est déjà pris';
            } else if (err.response.data && err.response.data.message) {
              this.generalError = err.response.data.message;
            } else {
              this.generalError = 'Erreur lors de la création du compte';
            }
          } else {
            this.generalError = 'Erreur de connexion au serveur';
          }
        });
    }
  }
};
</script>

<style scoped>
.invitation-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card {
  border: none;
  border-radius: 15px;
}

.invitation-info {
  border-left: 4px solid #0d6efd;
}

.bi-person-check {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
</style>
