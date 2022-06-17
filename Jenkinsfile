node('I_LOVE_IT') {
    def SCM_VARS
    stage('Git Clone') {
        echo "===================== Cloning from Git ======================="
        SCM_VARS =
                git(
                branch: 'release',
                credentialsId: 'e7fe12eb-4666-4cd0-af62-5d18b1c55756',
                url: 'git@github.com:FISOLUTION/ILUVIT_BACK.git'
                )
    }

    stage('has Changed?') {
        def CHANGE
        script {
            CHANGE = java.lang.String.valueOf(currentBuild.changeSets.size())
            if(CHANGE.equals('0')) {
                echo "===================== file does not Changed ====================="
                currentBuild.result = 'SUCCESS'
                sh "exit 1"
            }
        }
        echo CHANGE
    }

    stage('kill ex-Application'){
        BUILD_JAR = sh(encoding: 'UTF-8', returnStdout: true, script: "ls ./build/libs/*.jar")
        JAR_NAME = sh(encoding: 'UTF-8', returnStdout: true, script: "basename $BUILD_JAR")
        echo "$JAR_NAME"
        pid = sh(encoding: 'UTF-8', returnStdout: true, script: "pidof $JAR_NAME")
        echo "$pid"
        script {
            if (!pid.equals("")) {
                echo "===================== Killing Process ====================="
                sh "kill -15 $pid"
            } else {
                echo "===================== Nothing To Kill ====================="
            }
        }
    }

    stage('Access To Jar') {
        echo "===================== Access ====================="
        dir("./build/libs") {
            sh "pwd"
            sh "nohup java -jar iLUVit-0.0.1-SNAPSHOT.jar &"
            sh "tail -f nohup.out"

        }
    }
}