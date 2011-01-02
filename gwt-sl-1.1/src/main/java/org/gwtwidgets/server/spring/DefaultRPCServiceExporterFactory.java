/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gwtwidgets.server.spring;

/**
 * Returns for each invocation a new {@link GWTRPCServiceExporter} 
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 * 
 */
public class DefaultRPCServiceExporterFactory implements RPCServiceExporterFactory{

	private boolean responseCompressionEnabled;
	
	public void setResponseCompressionEnabled(boolean responseCompressionEnabled) {
		this.responseCompressionEnabled = responseCompressionEnabled;
	}

	/**
	 * Constructor for the default Factory
	 */
	public DefaultRPCServiceExporterFactory(){
	}
	
	/**
	 * Returns a new instance of an {@link RPCServiceExporter}
	 * @return {@link RPCServiceExporter}
	 */
	public RPCServiceExporter create() {
		GWTRPCServiceExporter exporter = new GWTRPCServiceExporter();
		exporter.setCompressResponse(responseCompressionEnabled?GWTRPCServiceExporter.COMPRESSION_AUTO:GWTRPCServiceExporter.COMPRESSION_DISABLED);
		return exporter;
	}

}
