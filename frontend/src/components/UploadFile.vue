<template>
    <div class="container">
        <div> {{checkboxesValues}}</div>
        <div>
            <button type="button" @click="selectAll()">SELECT ALL</button>
        </div>
        <div>
            <label>File
                <input type="file" id="file" ref="uploadFile" v-on:change="handleFileUpload()"/>
            </label>
        </div>
        <button type="button" @click="sendPageNumbers()" class="send">SPLIT</button>
        <div class="template" v-if="imagesLoaded">
            <!--images array of image links. Image = single page. Page start from 0.
                because PDF-Box at backend count pages from 0.
                But in real world page count from 1. User see page like in real world in div block page-number.
                But under the hood v-for index = page number = сheckbox value-->
            <div v-for="(image, i) of images" v-bind:key="image">
                <div class="page" v-if="images[i] !== null" draggable="true">
                    <label class="checkbox">
                        <input v-model="checkboxesValues" :value="i" type="checkbox"
                               class="form-check-input">
                    </label>
                    <div class="page-number">{{i}}</div>
                    <div class="delete">
                        <button @click="deletePage(i)" class="delete-button" type="button">&#10060;</button>
                    </div>
                    <img draggable="false" @click="clickCheckbox(i)" :src="image" :alt="image"/>
                </div>
            </div>
        </div>
        <div v-if="fileReady"><a :href="fileLink">DOWNLOAD</a></div>
    </div>
</template>


<script>

    import axios from "axios";

    export default {
        name: "UploadFile",
        setup() {

        },
        data() {
            return {
                uploadFile: "",
                images: [],
                imagesLoaded: false,
                checkboxesValues: [],
                allSelected: false,
                fileLink: "",
                fileReady: false,
            }
        },
        methods: {
            clickCheckbox(value) {
                const index = this.checkboxesValues.indexOf(value);
                if (index === -1) {
                    this.checkboxesValues.push(value);
                } else {
                    this.checkboxesValues.splice(index, 1);
                }
            },
            selectAll() {
                if (this.allSelected === false) {
                    this.allSelected = true;
                    this.checkboxesValues = [];
                    for (let i = 0; i < this.images.length; i++) {
                        if (this.images[i] !== null) {
                            this.checkboxesValues.push(i);
                        }
                    }
                } else {
                    this.allSelected = false;
                    this.checkboxesValues = [];
                }

            },
            deletePage(i) {
                /*this.images[i] = null
                if user delete page, the order should not change.
                because then v-for index will be recalculated and
                the wrong page numbers will go to the backend.
                image.length must be constant
                */
                const index = this.checkboxesValues.indexOf(i);
                this.checkboxesValues.splice(index, 1);
                this.images[i] = null;
            },
            submitFile() {
                //Initialize the form data
                let formData = new FormData();
                //Add the form data we need to submit
                formData.append('file', this.uploadFile);
                //Make the request to the POST /single-uploadFile URL
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
            //Handles a change on the uploadFile upload
            handleFileUpload() {
                this.uploadFile = this.$refs.uploadFile.files[0];
                this.submitFile();
            },
            sendPageNumbers() {
                axios.post('http://192.168.3.2:6060/split', this.checkboxesValues
                ).then(response => {
                    this.fileLink = response.data;
                    this.fileReady = true;
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

    .send {
        display: inline-block;
        width: 100px;
        height: 30px;
        margin-top: 10px;
        margin-bottom: 10px;
    }
</style>