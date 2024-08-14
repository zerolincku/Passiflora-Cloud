import java.io.File
import org.yaml.snakeyaml.Yaml;

fun configMap(path: String, env: String, projectVersion: String): MutableMap<String, String> {
    val configMap = mutableMapOf<String, String>()
    configMap["projectVersion"] = projectVersion
    configMap["env"] = env
    val configFile = File(path)
    val ymlMap = Yaml().loadAs(configFile.inputStream(), HashMap::class.java)
    processConfigMap(configMap, ymlMap.get(configMap["env"]) as Map<String, Any>)
    return configMap
}

fun processConfigMap(configMap: MutableMap<String, String>, map: Map<String, Any>, parentKey: String = "") {
    map.forEach { key, value ->
        val keyWithPrefix = if (parentKey.isEmpty()) key else "${parentKey}_${key}"
        when (value) {
            is Map<*, *> -> {
                // 如果值是Map，则递归调用processMap
                processConfigMap(configMap, value as Map<String, Any>, keyWithPrefix)
            }
            else -> {
                // 如果值不是Map，则将其添加到configMap中
                configMap[keyWithPrefix] = value.toString()
            }
        }
    }
}


