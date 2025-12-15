<template>
  <div>
    <h2><i class="bi bi-briefcase"></i> Gérer mes activités</h2>
    
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

    <div v-else>
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h4>{{ activities.length }} activité(s)</h4>
        <button class="btn btn-success" @click="showAddForm = true" :disabled="editingActivity !== null">
          <i class="bi bi-plus-circle"></i> Ajouter une activité
        </button>
      </div>

      <!-- Formulaire d'ajout -->
      <div v-if="showAddForm" class="card mb-4">
        <div class="card-header bg-success text-white">
          <h5 class="mb-0">Nouvelle activité</h5>
        </div>
        <div class="card-body">
          <form @submit.prevent="addActivity">
            <div class="row g-3">
              <div class="col-md-6">
                <label class="form-label">Titre *</label>
                <input 
                  type="text" 
                  class="form-control" 
                  v-model="newActivity.title" 
                  required
                />
              </div>
              
              <div class="col-md-3">
                <label class="form-label">Année *</label>
                <input 
                  type="number" 
                  class="form-control" 
                  v-model="newActivity.activityYear" 
                  min="1900" 
                  :max="new Date().getFullYear()"
                  required
                />
              </div>
              
              <div class="col-md-3">
                <label class="form-label">Nature *</label>
                <select class="form-select" v-model="newActivity.nature" required>
                  <option value="EXPERIENCE_PROFESSIONAL">Expérience Pro</option>
                  <option value="TRAINING">Formation</option>
                  <option value="PROJECTS">Projet</option>
                  <option value="OTHER">Autre</option>
                </select>
              </div>
              
              <div class="col-12">
                <label class="form-label">Description</label>
                <textarea 
                  class="form-control" 
                  v-model="newActivity.description" 
                  rows="3"
                ></textarea>
              </div>
              
              <div class="col-12">
                <label class="form-label">Site web</label>
                <input 
                  type="url" 
                  class="form-control" 
                  v-model="newActivity.web"
                  placeholder="https://..."
                />
              </div>
            </div>

            <div class="mt-3">
              <button type="submit" class="btn btn-success me-2" :disabled="saving">
                <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
                <i v-else class="bi bi-save"></i>
                {{ saving ? 'Enregistrement...' : 'Enregistrer' }}
              </button>
              <button type="button" class="btn btn-secondary" @click="cancelAdd">
                <i class="bi bi-x"></i> Annuler
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Formulaire de modification -->
      <div v-if="editingActivity" class="card mb-4">
        <div class="card-header bg-warning text-dark">
          <h5 class="mb-0">Modifier l'activité</h5>
        </div>
        <div class="card-body">
          <form @submit.prevent="updateActivity">
            <div class="row g-3">
              <div class="col-md-6">
                <label class="form-label">Titre *</label>
                <input 
                  type="text" 
                  class="form-control" 
                  v-model="editingActivity.title" 
                  required
                />
              </div>
              
              <div class="col-md-3">
                <label class="form-label">Année *</label>
                <input 
                  type="number" 
                  class="form-control" 
                  v-model="editingActivity.activityYear" 
                  min="1900" 
                  :max="new Date().getFullYear()"
                  required
                />
              </div>
              
              <div class="col-md-3">
                <label class="form-label">Nature *</label>
                <select class="form-select" v-model="editingActivity.nature" required>
                  <option value="EXPERIENCE_PROFESSIONAL">Expérience Pro</option>
                  <option value="TRAINING">Formation</option>
                  <option value="PROJECTS">Projet</option>
                  <option value="OTHER">Autre</option>
                </select>
              </div>
              
              <div class="col-12">
                <label class="form-label">Description</label>
                <textarea 
                  class="form-control" 
                  v-model="editingActivity.description" 
                  rows="3"
                ></textarea>
              </div>
              
              <div class="col-12">
                <label class="form-label">Site web</label>
                <input 
                  type="url" 
                  class="form-control" 
                  v-model="editingActivity.web"
                  placeholder="https://..."
                />
              </div>
            </div>

            <div class="mt-3">
              <button type="submit" class="btn btn-warning me-2" :disabled="saving">
                <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
                <i v-else class="bi bi-save"></i>
                {{ saving ? 'Enregistrement...' : 'Enregistrer les modifications' }}
              </button>
              <button type="button" class="btn btn-secondary" @click="cancelEdit">
                <i class="bi bi-x"></i> Annuler
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Liste des activités -->
      <div v-if="activities.length === 0" class="alert alert-info">
        <i class="bi bi-info-circle"></i> Aucune activité enregistrée.
      </div>
      
      <div v-else class="row">
        <div v-for="activity in sortedActivities" :key="activity.id" class="col-md-6 mb-3">
          <div class="card h-100">
            <div class="card-body">
              <div class="d-flex justify-content-between align-items-start mb-2">
                <h5 class="card-title">
                  <span class="badge" :class="getNatureBadgeClass(activity.nature)">
                    {{ getNatureLabel(activity.nature) }}
                  </span>
                </h5>
                <span class="text-muted">{{ activity.activityYear }}</span>
              </div>
              
              <h6 class="mb-2">{{ activity.title }}</h6>
              
              <p class="card-text" v-if="activity.description">
                {{ activity.description }}
              </p>
              
              <p v-if="activity.web">
                <a :href="activity.web" target="_blank" class="btn btn-sm btn-outline-primary">
                  <i class="bi bi-link-45deg"></i> Voir le site
                </a>
              </p>
              
              <div class="mt-3">
                <button class="btn btn-sm btn-success me-2" @click="startEdit(activity)">
                  <i class="bi bi-pencil"></i> Modifier
                </button>
                <button class="btn btn-sm btn-danger" @click="deleteActivity(activity.id)">
                  <i class="bi bi-trash"></i> Supprimer
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <ConfirmDialog
      :show="confirmDelete.show"
      title="Confirmer la suppression"
      :message="`Êtes-vous sûr de vouloir supprimer l'activité :<br><strong>&quot;${confirmDelete.activity?.title}&quot;</strong> (${confirmDelete.activity?.activityYear})`"
      confirmText="Supprimer"
      cancelText="Annuler"
      type="danger"
      @confirm="confirmDeleteAction"
      @cancel="cancelDelete"
    />
  </div>
</template>

<script>
import axios from 'axios';
import ConfirmDialog from '../common/ConfirmDialog.vue';

export default {
  components: {
    ConfirmDialog
  },
  inject: ['user'],
  data() {
    return {
      person: null,
      activities: [],
      loading: true,
      saving: false,
      showAddForm: false,
      confirmDelete: {
        show: false,
        activity: null
      },
      editingActivity: null,
      newActivity: this.getEmptyActivity(),
      axiosInstance: null
    };
  },
  computed: {
    sortedActivities() {
      return [...this.activities].sort((a, b) => b.activityYear - a.activityYear);
    }
  },
  mounted() {
    this.loadMyCV();
  },
  methods: {
    getEmptyActivity() {
      return {
        title: '',
        activityYear: new Date().getFullYear(),
        nature: 'EXPERIENCE_PROFESSIONAL',
        description: '',
        web: ''
      };
    },
    
    loadMyCV() {
      this.loading = true;
      this.error = null;
      axios.get('/api/persons/me', {
        headers: {
          'Authorization': `Bearer ${this.user.token}`
        }
      })
      .then(response => {
        this.person = response.data;
        this.activities = this.person ? (this.person.activities || []) : [];
        this.loading = false;
      })
      .catch(err => {
        console.error('Erreur:', err);
        this.error = 'Impossible de charger votre CV';
        this.loading = false;
      });
    },

    addActivity() {
      if (!this.person || !this.person.id) {
        return;
      }
      
      this.saving = true;
      
      const activityData = {
        ...this.newActivity,
        person: { id: this.person.id }
      };
      
      axios.post('/api/activities', activityData, {
        headers: {
          'Authorization': `Bearer ${this.user.token}`
        }
      })
        .then(() => {
          this.saving = false;
          this.showAddForm = false;
          this.newActivity = this.getEmptyActivity();
          this.loadMyCV();
          alert('Activité ajoutée avec succès');
        })
        .catch(err => {
          console.error('Erreur:', err);
          this.saving = false;
          alert('Erreur lors de l\'ajout de l\'activité');
        });
    },
    
    cancelAdd() {
      this.showAddForm = false;
      this.newActivity = this.getEmptyActivity();
    },
    
    startEdit(activity) {
      this.editingActivity = { ...activity };
      this.showAddForm = false;
      
      this.$nextTick(() => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
      });
    },
    
    updateActivity() {
      if (!this.editingActivity || !this.editingActivity.id) {
        return;
      }
      
      this.saving = true;
      
      const activityData = {
        ...this.editingActivity,
        person: { id: this.person.id }
      };
      
      axios.put(`/api/activities/${this.editingActivity.id}`, activityData, {
        headers: {
          'Authorization': `Bearer ${this.user.token}`
        }
      })
        .then(() => {
          this.saving = false;
          this.editingActivity = null;
          this.loadMyCV();
          alert('Activité modifiée avec succès');
        })
        .catch(err => {
          console.error('Erreur:', err);
          this.saving = false;
          alert('Erreur lors de la modification de l\'activité: ' + (err.response?.data?.message || err.message));
        });
    },
    
    cancelEdit() {
      this.editingActivity = null;
    },
    
    deleteActivity(id) {
      const activity = this.activities.find(a => a.id === id);
      if (!activity) {
        alert('Erreur: Activité non trouvée');
        return;
      }
      
      this.confirmDelete.activity = activity;
      this.confirmDelete.show = true;
    },
    
    cancelDelete() {
      this.confirmDelete.show = false;
      this.confirmDelete.activity = null;
    },
    
    confirmDeleteAction() {
      const id = this.confirmDelete.activity.id;
      
      axios.delete(`/api/activities/${id}`, {
        headers: {
          'Authorization': `Bearer ${this.user.token}`
        }
      })
        .then(() => {
          this.loadMyCV();
          this.cancelDelete();
        })
        .catch(err => {
          alert('Erreur lors de la suppression: ' + (err.response?.data || err.message));
          this.cancelDelete();
        });
    },
    
    getNatureLabel(nature) {
      const labels = {
        'EXPERIENCE_PROFESSIONAL': 'Expérience Pro',
        'TRAINING': 'Formation',
        'PROJECTS': 'Projet',
        'OTHER': 'Autre'
      };
      return labels[nature] || nature;
    },
    
    getNatureBadgeClass(nature) {
      const classes = {
        'EXPERIENCE_PROFESSIONAL': 'bg-primary',
        'TRAINING': 'bg-success',
        'PROJECTS': 'bg-info',
        'OTHER': 'bg-secondary'
      };
      return classes[nature] || 'bg-secondary';
    }
  }
};
</script>

<style scoped>
.activity-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}
</style>
