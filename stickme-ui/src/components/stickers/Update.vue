<script>
import { useRoute } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';
import { required, maxLength, minValue, between } from '@vuelidate/validators';

export default {
    setup() {
        return {
            route: useRoute(),
            validator: useVuelidate({ $autoDirty: true })
        }
    },
    data() {
        return {
            id: this.route.params.id,
            sizes: [],
            aspects: [],
            inputs: {
                name: null,
                description: null,
                file: undefined,
                sizeId: 0,
                aspectId: 0,
                price: null
            }
        }
    },
    validations() {
        return {
            inputs: {
                name: { required, maxLength: maxLength(100) },
                description: { required, maxLength: maxLength(1000) },
                file: {
                    maxValue: (file) => {
                        return file ? file.size <= 1048576 : true;
                    }
                },
                sizeId: { minValue: minValue(1) },
                aspectId: { minValue: minValue(1) },
                price: { required, between: between(0.01, 100.0) }
            }
        }
    },
    methods: {
        fileSelected(event) {
            console.log(event.target.files[0]);
            this.inputs.file = event.target.files[0];
        },
        async submit() {
            console.log(this.inputs.file);
            const formData = new FormData();
            Object.keys(this.inputs).forEach((key) => {
                const value = this.inputs[key];
                if (value) {
                    formData.append(key, value);
                }
            });
            const resp = await this.$http.put(`/stickers/${this.id}`, formData);
            if (resp.status === 204) {
                this.$toast.success('toast-global', 'Sticker updated with success.');
                this.$router.push({ name: 'stickers-edit' });
            } else {
                console.error(resp);
                this.$toast.error('toast-global', 'Server conversion or validation error.');
            }
        },
        async initSizes() {
            const resp = await this.$http.get('/sizes');
            this.sizes = resp.body;
        },
        async initAspects() {
            const resp = await this.$http.get('/aspects');
            this.aspects = resp.body;
        },
        async initInputs() {
            const resp = await this.$http.get(`/stickers/${this.id}/for-update`);
            this.inputs = resp.body;
        }
    },
    beforeMount() {
        this.initSizes();
        this.initAspects();
        this.initInputs();
    }
}
</script>

<template>
    <h1>Update sticker</h1>
    <form @submit.prevent="submit" novalidate>
        <div class="row mb-3">
            <div class="col-12">
                <label for="name" class="form-label required">Name</label>
                <input v-model.trim="inputs.name" id="name" name="name" type="text" class="form-control"
                    :class="{ 'is-invalid': validator.inputs.name.$error }">
                <div class="form-text">Must be a unique sticker name and not exceed 100 characters.</div>
            </div>
        </div>
        <div class="row mb-3">
            <div class="col-12">
                <label for="description" class="form-label required">Description</label>
                <textarea v-model.trim="inputs.description" id="description" name="description" rows="5"
                    class="form-control" :class="{ 'is-invalid': validator.inputs.description.$error }"></textarea>
                <div class="form-text">Must not exceed 1000 characters.</div>
            </div>
        </div>
        <div class="row mb-3">
            <div class="col-12">
                <label for="file" class="form-label">Image</label>
                <input @change="fileSelected" accept="image/jpeg,image/png,image/gif" id="file" name="file" type="file"
                    class="form-control" :class="{ 'is-invalid': validator.inputs.file.$error }">
                <div class="form-text">Must be JPEG, PNG or GIF, and not exceed 1MB. Will replace current image.</div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-4 mb-3">
                <label for="sizeId" class="form-label required">Size</label>
                <select v-model.number="inputs.sizeId" id="sizeId" name="sizeId" class="form-select"
                    :class="{ 'is-invalid': validator.inputs.sizeId.$error }">
                    <LabelValues :items="sizes" />
                </select>
            </div>
            <div class="col-md-4 mb-3">
                <label for="aspectId" class="form-label required">Aspect</label>
                <select v-model.number="inputs.aspectId" id="aspectId" name="aspectId" class="form-select"
                    :class="{ 'is-invalid': validator.inputs.aspectId.$error }">
                    <LabelValues :items="aspects" />
                </select>
            </div>
            <div class="col-md-4 mb-3">
                <label for="price" class="form-label required">Price</label>
                <div class="input-group">
                    <span class="input-group-text">€</span>
                    <input v-model.number="inputs.price" id="price" name="price" type="number" class="form-control"
                        :class="{ 'is-invalid': validator.inputs.price.$error }">
                </div>
                <div class="form-text">Must not exceed €100.</div>
            </div>
            <div class="d-grid d-md-flex justify-content-md-end mb-3">
                <button type="submit" class="btn btn-dark" :disabled="validator.$invalid">Sauvegarder</button>
            </div>
        </div>
    </form>
</template>
