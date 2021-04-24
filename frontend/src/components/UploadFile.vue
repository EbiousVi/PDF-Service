<template>
    <div class="container">
        <div> {{checkboxValues}}</div>
        <div>
            <button class="button" type="button" @click="selectAll()">SELECT ALL</button>
        </div>
        <div>
            <label>SELECT PDF FILE
                <input class="" type="file" id="file" ref="uploadFile" v-on:change="handleFileUpload()"/>
            </label>
        </div>
        <button class="button" type="button" @click="sendPageNumbers()">SPLIT</button>
        <div class="template" v-if="imagesLoaded">
            <!--images array of image links. Image = single page. Page start from 0.
                because PDF-Box at backend count pages from 0.
                But in real world page count start from 1. User see page like in real world in div block page-number.
                But under the hood v-for index = page number = сheckbox value-->
            <div v-for="(image, i) of images" v-bind:key="image">
                <div class="page" v-if="images[i] !== null" draggable="true">
                    <label class="checkbox">
                        <input v-model="checkboxValues" :value="i" type="checkbox"
                               class="form-check-input" required>
                    </label>
                    <div class="page-number">{{i}}</div>
                    <div class="delete">
                        <button @click="deletePage(i)" class="delete-button" type="button">&#10060;</button>
                    </div>
                    <img @click="clickCheckbox(i)" :src="image" :alt="image"/>
                </div>
            </div>
        </div>
        <div v-if="fileLoaded"><a :href="splitFile">DOWNLOAD</a></div>
    </div>
</template>


<script>
    import axios from "axios";

    export default {
        name: "UploadFile",
        data() {
            return {
                uploadFile: "",
                images: [],
                imagesLoaded: false,
                checkboxValues: [],
                allSelected: false,
                splitFile: "",
                fileLoaded: false,
            }
        },
        methods: {
            clickCheckbox(value) {
                const index = this.checkboxValues.indexOf(value);
                if (index === -1) {
                    this.checkboxValues.push(value);
                } else {
                    this.checkboxValues.splice(index, 1);
                }
            },
            selectAll() {
                if (this.allSelected === false) {
                    this.allSelected = true;
                    this.checkboxValues = [];
                    for (let i = 0; i < this.images.length; i++) {
                        if (this.images[i] !== null) {
                            this.checkboxValues.push(i);
                        }
                    }
                } else {
                    this.allSelected = false;
                    this.checkboxValues = [];
                }
            },
            deletePage(i) {
                /*this.images[i] = null
                if user delete page, the order should not change.
                because then v-for index will be recalculated and
                the wrong page numbers will go to the backend.
                image.length must be constant
                */
                const index = this.checkboxValues.indexOf(i);
                this.checkboxValues.splice(index, 1);
                this.images[i] = null;
            },
            submitFile() {
                let formData = new FormData();
                formData.append('file', this.uploadFile);
                axios.post('http://192.168.3.2:6060/upload-single', formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    }
                ).then(response => {
                    this.images = response.data;
                    this.imagesLoaded = true;
                });
            },
            handleFileUpload() {
                this.uploadFile = this.$refs.uploadFile.files[0];
                this.submitFile();
            },
            sendPageNumbers() {
                axios.post('http://192.168.3.2:6060/split', this.checkboxValues
                ).then(response => {
                    this.splitFile = response.data;
                    this.fileLoaded = true;
                    this.imagesLoaded = false;
                });
            }
        }
    }
</script>

<style scoped>
    img {
        width: 160px;
        height: 250px;
    }

    .template {
        display: flex;
        flex-wrap: wrap;
        margin-left: 100px;
        margin-right: 100px;
    }

    .page {
        position: relative;
        margin-left: 15px;
        margin-right: 15px;
    }

    .page-number {
        position: absolute;
        left: 0;
        bottom: 0;
        width: 25px;
        height: 25px;;
        background-color: gray;
        color: black;
        text-align: center;
        font-weight: bold;
    }

    .delete {
        position: absolute;
        right: 0;
        width: 25px;
        height: 25px;
    }

    .delete-button {

    }

    .checkbox {
        position: absolute;
        left: 0;
    }

    .form-check-input {
        width: 25px;
        height: 25px;
    }

    .button {
        background-color: #ffffff;
        color: black;
        padding: 10px 20px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 16px;
        margin: 4px 2px;
        transition-duration: 0.4s;
        cursor: pointer;
        border: 2px solid black;
        border-radius: 2px;
    }

    .button:hover {
        background-color: black;
        color: white;
    }

    a:link {
        background-color: white;
        color: black;
        padding: 10px 20px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 16px;
        margin: 4px 2px;
        transition-duration: 0.4s;
        cursor: pointer;
        border: 2px solid black;
        border-radius: 2px;
    }

    a:hover, a:active, a:visited {
        background-color: greenyellow;
    }
</style>