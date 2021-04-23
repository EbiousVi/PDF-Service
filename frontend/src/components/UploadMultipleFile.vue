<template>
    <div>
        <label>Files
            <input type="file" ref="files" multiple v-on:change="handleFilesUpload()"/>
        </label>
    </div>
</template>

<script>
    import axios from "axios";

    export default {
        name: "UploadMultipleFile",
        data() {
            return {
                files: ''
            }
        },
        methods: {
            submitFiles() {

                let formData = new FormData();
                /*
                  Iteate over any file sent over appending the files
                  to the form data.
                */
                for (let i = 0; i < this.files.length; i++) {
                    let file = this.files[i];
                    formData.append("file", file);
                }
                /*
                  Make the request to the POST /multiple-files URL
                */
                axios.post('http://192.168.3.2:6060/upload-multiple',
                    formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    }
                ).then(function () {
                    console.log('SUCCESS!!');
                })
                    .catch(function () {
                        console.log('FAILURE!!');
                    });
            },

            /*
              Handles a change on the file upload
            */
            handleFilesUpload() {
                this.files = this.$refs.files.files;
                this.submitFiles();
            }
        }
    }
</script>

<style scoped>

</style>