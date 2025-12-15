<template>
  <div>
    <h2><i class="bi bi-pencil-square"></i> Modifier mon CV</h2>
    
    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
    </div>

    <div v-else-if="!person" class="alert alert-warning">
      <i class="bi bi-info-circle"></i> 
      Vous n'avez pas encore de CV. 
      <a href="#/person/new" class="alert-link">Créer un CV</a>
    </div>

    <div v-else class="card">
      <div class="card-header bg-primary text-white">
        <h4 class="mb-0">Modification de mon CV</h4>
      </div>
      <div class="card-body">
        <form @submit.prevent="savePerson">
          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label">Prénom *</label>
              <input 
                type="text" 
                class="form-control" 
                v-model="person.firstName" 
                required
              />
            </div>
            
            <div class="col-md-6">
              <label class="form-label">Nom *</label>
              <input 
                type="text" 
                class="form-control" 
                v-model="person.lastName" 
                required
              />
            </div>
            
            <div class="col-md-6">
              <label class="form-label">Email *</label>
              <input 
                type="email" 
                class="form-control" 
                v-model="person.email" 
                required
                disabled
                title="L'email ne peut pas être modifié"
              />
              <small class="text-muted">L'email ne peut pas être modifié</small>
            </div>
            
            <div class="col-md-6">
              <label class="form-label">Site Web</label>
              <input 
                type="url" 
                class="form-control" 
                v-model="person.website"
                placeholder="https://..."
              />
            </div>
            
            <div class="col-md-6">
              <label class="form-label">Date de naissance</label>
              <input 
                type="date" 
                class="form-control" 
                v-model="person.birthDate"
              />
            </div>
          </div>

          <div class="mt-4">
            <button type="submit" class="btn btn-primary me-2" :disabled="saving">
              <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="bi bi-save"></i>
              {{ saving ? 'Enregistrement...' : 'Enregistrer' }}
            </button>
            <a href="#/my-cv/view" class="btn btn-secondary">
              <i class="bi bi-x"></i> Annuler
            </a>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  inject: ['user'],
  data() {
    return {
      person: null,
      loading: true,
      saving: false,
      axiosInstance: null
    };
  },
  mounted() {
    this.axiosInstance = axios.create({
      baseURL: '/api/',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${this.user.token}`
      },
      timeout: 5000,
    });
    this.loadMyCV();
  },
  methods: {
    loadMyCV() {
      this.loading = true;
      
      this.axiosInstance.get('persons/me')
        .then(response => {
          this.person = response.data;
          this.loading = false;
        })
        .catch(err => {
          console.error('Erreur:', err);
          alert('Impossible de charger votre CV');
          this.loading = false;
        });
    },
    
    savePerson() {
      if (!this.person || !this.person.id) {
        return;
      }
      
      this.saving = true;
      
      this.axiosInstance.put(`persons/${this.person.id}`, this.person)
        .then(() => {
          this.saving = false;
          alert('CV mis à jour avec succès');
          window.location.hash = '#/my-cv/view';
        })
        .catch(err => {
          console.error('Erreur:', err);
          this.saving = false;
          alert('Erreur lors de la mise à jour du CV');
        });
    }
  }
};
</script>
