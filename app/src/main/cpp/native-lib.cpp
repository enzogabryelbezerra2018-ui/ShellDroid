#include <jni.h>
#include <string>
#include <android/log.h>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "shelldroid-native", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "shelldroid-native", __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_seudominio_shelldroid_MainActivity_stringFromNative(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    LOGI("native initialized");
    return env->NewStringUTF(hello.c_str());
}

// Exemplo: função que recebe comando e devolve uma string (nativo)
extern "C" JNIEXPORT jstring JNICALL
Java_com_seudominio_shelldroid_MainActivity_runNativeEcho(
        JNIEnv* env,
        jobject /* this */,
        jstring input) {
    const char* cstr = env->GetStringUTFChars(input, nullptr);
    std::string out = std::string("NATIVE ECHO: ") + (cstr ? cstr : "");
    env->ReleaseStringUTFChars(input, cstr);
    return env->NewStringUTF(out.c_str());
}
