/**
 * Copyright (C) 2014 Federico Recio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.com.pfinfo.swagger;

import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;

import java.util.Optional;

/**
 * Wrapper around Dropwizard's configuration and the bundle's config that simplifies getting some
 * information from them.
 *
 * @author Federico Recio
 * @author Flemming Frandsen
 */
public class ConfigurationHelper {

    private final Configuration configuration;
    private final SwaggerBundleConfiguration swaggerBundleConfiguration;
    private final Environment environment;

    public ConfigurationHelper(Configuration configuration, SwaggerBundleConfiguration swaggerBundleConfiguration) {
        this(configuration,swaggerBundleConfiguration,null);
    }
    public ConfigurationHelper(Configuration configuration, SwaggerBundleConfiguration swaggerBundleConfiguration, Environment environment) {
        this.configuration = configuration;
        this.swaggerBundleConfiguration = swaggerBundleConfiguration;
        this.environment=environment;
    }

    public String getJerseyRootPath() {
        // if the user explictly defined a path to prefix requests use it instead of derive it
        if (swaggerBundleConfiguration.getUriPrefix() != null) {
            return swaggerBundleConfiguration.getUriPrefix();
        }
        final Optional<String> rootPath = Optional.of(configuration.getHttpConfiguration().getRootPath());

        return stripUrlSlashes(rootPath.orElse("/"));
    }

    public String getUrlPattern() {
        // if the user explictly defined a path to prefix requests use it instead of derive it
        if (swaggerBundleConfiguration.getUriPrefix() != null) {
            return swaggerBundleConfiguration.getUriPrefix();
        }

        final String applicationContextPath = getApplicationContextPath();
        final String rootPath = getJerseyRootPath();

        String urlPattern;

        if (rootPath.equals("/") && applicationContextPath.equals("/")) {
            urlPattern = "/";
        } else if (rootPath.equals("/") && !applicationContextPath.equals("/")) {
            urlPattern = applicationContextPath;
        } else if (!rootPath.equals("/") && applicationContextPath.equals("/")) {
            urlPattern = rootPath;
        } else {
            urlPattern = applicationContextPath + rootPath;
        }

        return urlPattern;
    }

    public String getSwaggerUriPath() {
        final String jerseyRootPath = getJerseyRootPath();
        String uriPathPrefix = jerseyRootPath.equals("/") ? "" : jerseyRootPath;
        return uriPathPrefix + Constants.SWAGGER_URI_PATH;
    }

    private String getApplicationContextPath() {
        String applicationContextPath = "/application";
        if(environment!=null){
            applicationContextPath = environment.getJerseyServletContainer().getServletContext().getContextPath();
        }
        return stripUrlSlashes(applicationContextPath);
    }

    private String stripUrlSlashes(String urlToStrip) {
        if (urlToStrip.endsWith("/*")) {
            urlToStrip = urlToStrip.substring(0, urlToStrip.length() - 1);
        }

        if (urlToStrip.length() > 1 && urlToStrip.endsWith("/")) {
            urlToStrip = urlToStrip.substring(0, urlToStrip.length() - 1);
        }

        return urlToStrip;
    }
}
