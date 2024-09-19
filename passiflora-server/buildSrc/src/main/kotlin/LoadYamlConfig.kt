import java.io.File
import org.yaml.snakeyaml.Yaml
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@Suppress("unchecked_cast")
fun configMap(path: String, env: String, projectVersion: String): MutableMap<String, String> {
    val configMap = mutableMapOf<String, String>()
    configMap["projectVersion"] = projectVersion
    configMap["env"] = env
    // 添加编译时间
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    configMap["buildTime"] = dateFormat.format(Date())
    val configFile = File(path)
    val ymlMap = Yaml().loadAs(configFile.inputStream(), HashMap::class.java)
    processConfigMap(configMap, ymlMap.get(configMap["env"]) as Map<String, Any>)
    return configMap
}

@Suppress("unchecked_cast")
fun processConfigMap(configMap: MutableMap<String, String>, map: Map<String, Any>, parentKey: String = "") {
    map.forEach { key, value ->
        val keyWithPrefix = if (parentKey.isEmpty()) key else "${parentKey}.${key}"
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


