/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.start.extension.springcloud;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.project.documentation.HelpDocument;
import io.spring.initializr.generator.project.documentation.HelpDocumentCustomizer;

/**
 * {@link HelpDocumentCustomizer} for Spring cloud stream.
 *
 * @author Madhura Bhave
 */
class SpringCloudStreamHelpDocumentCustomizer implements HelpDocumentCustomizer {

	private final Build build;

	SpringCloudStreamHelpDocumentCustomizer(Build build) {
		this.build = build;
	}

	@Override
	public void customize(HelpDocument document) {
		if (hasSpringCloudStream()) {
			document.gettingStarted().addRequiredDependency("Binder",
					"A binder is required for the application to start up, e.g, RabbitMQ or Kafka");
		}
	}

	private boolean hasSpringCloudStream() {
		return this.build.dependencies().values()
				.anyMatch((dependency) -> requiresBinder(dependency.getArtifactId()));
	}

	private boolean requiresBinder(String artifactId) {
		return artifactId.equals("spring-cloud-bus")
				|| artifactId.equals("spring-cloud-stream")
				|| artifactId.equals("spring-cloud-stream-reactive");
	}

}
