package org.infinispan.server.test.task.servertask;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.scripting.ScriptingManager;
import org.infinispan.scripting.impl.ScriptingManagerImpl;
import org.infinispan.tasks.ServerTask;
import org.infinispan.tasks.TaskContext;
import org.infinispan.test.TestingUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Server Task executing JavaScript script.
 *
 * @author amanukya
 */
public class JSExecutingServerTask implements ServerTask {
    public static final String NAME = "jsexecutor_task";
    public static final String CACHE_NAME = "taskAccessible";

    private TaskContext taskContext;

    @Override
    public void setTaskContext(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object call() throws Exception {
        Cache cache = taskContext.getCache().get();
        EmbeddedCacheManager cacheManager = cache.getCacheManager();
        Cache usedCache = cacheManager.getCache(CACHE_NAME);

        ScriptingManager scriptingManager = cacheManager.getGlobalComponentRegistry().getComponent(ScriptingManager.class);
        loadScript(scriptingManager, "/stream_serverTask.js");

        Map<String, Long> result = (Map<String, Long>) scriptingManager.runScript("/stream_serverTask.js",
                new TaskContext().cache(usedCache).marshaller(taskContext.getMarshaller().get())).get();
        return result;
    }

    private void loadScript(ScriptingManager scriptingManager, String scriptName) throws IOException {
        try (InputStream is = JSExecutingServerTask.class.getResourceAsStream("/" + scriptName)) {
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            for (String line = r.readLine(); line != null; line = r.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
            String script = sb.toString();

            scriptingManager.addScript(scriptName, script);
        }
    }
}