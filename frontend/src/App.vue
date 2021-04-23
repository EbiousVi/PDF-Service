<template>
    <upload-file v-if="false"></upload-file>
    <upload-multiple-file></upload-multiple-file>
</template>

<script>

    import UploadFile from "./components/UploadFile";
    import UploadMultipleFile from "./components/UploadMultipleFile";

    export default {
        name: 'App',
        components: {
            UploadMultipleFile,
            UploadFile,

        },
        data() {
            return {}
        },
    }
</script>

<style>
    #app {
        font-family: Avenir, Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        color: #2c3e50;
        margin-top: 60px;
    }
</style>
