node('I_LOVE_IT') {
    def SCM_VARS
    stage('Git Clone') {
        SCM_VARS =
                git(
                branch: 'develop',
                credentialsId: 'jenkins-github-wh',
                url: 'git@github.com:FISOLUTION/ILUVIT_BACK.git'
                )
    }
    stage('has Changed?') {
        def CHANGE = sh(script: "git diff ${SCM_VARS.GIT_PREVIOUS_SUCCESSFUL_COMMIT} ${SCM_VARS.GIT_COMMIT} test.txt", returnStdout: true)

        script {
            if (CHANGE.length() <= 0) {
                sh exit
            }
        }
    }
}