package org.example;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baidu.titan.dex.DexItemFactory;
import com.baidu.titan.dex.MultiDexFileBytes;
import com.baidu.titan.dex.node.DexClassNode;
import com.baidu.titan.dex.node.DexCodeNode;
import com.baidu.titan.dex.node.DexFieldNode;
import com.baidu.titan.dex.node.DexFileNode;
import com.baidu.titan.dex.node.DexMethodNode;
import com.baidu.titan.dex.node.MultiDexFileNode;
import com.baidu.titan.dex.reader.MultiDexFileReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tong
 */
public class Save_Class_method_field_info {
    public static void main(String[] args) throws IOException {
        DexItemFactory dexFactory = new DexItemFactory();

        MultiDexFileBytes mdfb = MultiDexFileBytes.createFromZipFile(new File("/Users/tong/Projects/robust/robustplus-sdk/app/robust/app-debug.apk"));

// dex reader
        MultiDexFileReader mdReader = new MultiDexFileReader(dexFactory);
        mdfb.forEach((dexId, dexBytes) -> {
            mdReader.addDexContent(dexId, dexBytes.getDexFileBytes());
        });


        final Map<String, Map<String, Object>> result = new HashMap<>();

        // dex node
        MultiDexFileNode mdfn = new MultiDexFileNode();
        mdReader.accept(mdfn.asVisitor());
        Map<Integer, DexFileNode> dexFiles = mdfn.getDexNodes();
        dexFiles.forEach((dexId, dexFileNode) -> {
            List<DexClassNode> classes = dexFileNode.getClassesList();

            for (DexClassNode aClass : classes) {
                Map<String, Object> classInfo = new HashMap<>();
                Map<String, Object> methodInfo = new HashMap<>();
                Map<String, Object> fieldInfo = new HashMap<>();
                classInfo.put("fieldInfo", fieldInfo);
                classInfo.put("methodInfo", methodInfo);

                result.put(aClass.type.toTypeDescriptor(), classInfo);

                classInfo.put("dexId", dexId);
                classInfo.put("accFlags", aClass.accessFlags.getFlags());
                if (aClass.sourceFile != null) {
                    classInfo.put("sourceFile", aClass.sourceFile.toString());
                }
                for (DexFieldNode field : aClass.getFields()) {
                    Map<String, Object> info = new HashMap<>();

                    //info.put("name", field.name.toString());
                    info.put("accFlags", field.accessFlags.getFlags());
                    info.put("t", field.type.toTypeDescriptor());
                    fieldInfo.put(field.name.toString(), info);
                }
                for (DexMethodNode method : aClass.getMethods()) {

                    DexCodeNode code = method.getCode();

                    Map<String, Object> info = new HashMap<>();
                    StringBuilder sb = new StringBuilder();
                    sb.append(method.name.toString());
                    sb.append("(");
                    if (method.parameters != null) {
                        for (int i = 0; i < method.parameters.count(); i++) {
                            sb.append(method.parameters.getType(i).toTypeDescriptor());
                        }
                    }
                    sb.append(")");
                    sb.append(method.returnType.toTypeDescriptor());

                    //info.put("name", method.name.toString());
                    info.put("accFlags", method.accessFlags.getFlags());
                    methodInfo.put(sb.toString(), info);
                }
            }
            //System.out.println(JSONArray.toJSON(classes));
        });

        FileOutputStream fos = new FileOutputStream(".test/ref.json");
        fos.write(JSONArray.toJSONString(result, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat).getBytes(StandardCharsets.UTF_8));
        fos.flush();
        fos.close();
    }
}