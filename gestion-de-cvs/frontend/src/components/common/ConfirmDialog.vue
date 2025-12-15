<template>
  <div v-if="show" class="modal-overlay" @click.self="onCancel">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header" :class="headerClass">
          <h5 class="modal-title">
            <i :class="iconClass"></i> {{ title }}
          </h5>
          <button type="button" class="btn-close" :class="closeButtonClass" @click="onCancel"></button>
        </div>
        <div class="modal-body">
          <p class="mb-0" v-html="message"></p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="onCancel">
            <i class="bi bi-x-circle"></i> {{ cancelText }}
          </button>
          <button type="button" class="btn" :class="confirmButtonClass" @click="onConfirm">
            <i :class="confirmIconClass"></i> {{ confirmText }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ConfirmDialog',
  props: {
    show: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: 'Confirmation'
    },
    message: {
      type: String,
      required: true
    },
    confirmText: {
      type: String,
      default: 'Confirmer'
    },
    cancelText: {
      type: String,
      default: 'Annuler'
    },
    type: {
      type: String,
      default: 'danger', // 'danger', 'warning', 'info', 'success'
      validator: (value) => ['danger', 'warning', 'info', 'success'].includes(value)
    }
  },
  computed: {
    headerClass() {
      const classes = {
        danger: 'bg-danger text-white',
        warning: 'bg-warning text-dark',
        info: 'bg-info text-white',
        success: 'bg-success text-white'
      };
      return classes[this.type];
    },
    confirmButtonClass() {
      return `btn-${this.type}`;
    },
    iconClass() {
      const icons = {
        danger: 'bi bi-exclamation-triangle',
        warning: 'bi bi-exclamation-circle',
        info: 'bi bi-info-circle',
        success: 'bi bi-check-circle'
      };
      return icons[this.type];
    },
    confirmIconClass() {
      const icons = {
        danger: 'bi bi-trash',
        warning: 'bi bi-exclamation-triangle',
        info: 'bi bi-check',
        success: 'bi bi-check'
      };
      return icons[this.type];
    },
    closeButtonClass() {
      return this.type === 'warning' ? '' : 'btn-close-white';
    }
  },
  methods: {
    onConfirm() {
      this.$emit('confirm');
    },
    onCancel() {
      this.$emit('cancel');
    }
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1050;
  animation: fadeIn 0.2s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-dialog {
  max-width: 500px;
  width: 90%;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    transform: translateY(-50px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
}
</style>
