/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 *
 * If you like this project or if you find it useful, you can support us at:
 *
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 *
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 *
 */
package net.sourceforge.plantuml.tim;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.DefinitionsContainer;
import net.sourceforge.plantuml.log.Logme;
import net.sourceforge.plantuml.preproc.PreprocessingArtifact;
import net.sourceforge.plantuml.preproc.Defines;
import net.sourceforge.plantuml.preproc.FileWithSuffix;
import net.sourceforge.plantuml.preproc.ImportedFiles;
import net.sourceforge.plantuml.text.StringLocated;

public class TimLoader {

	private final TContext context;
	private final TMemory global = new TMemoryGlobal();
	private boolean preprocessorError;
	private List<StringLocated> resultList;
	private PreprocessingArtifact preprocessingArtifact;

	public TimLoader(ImportedFiles importedFiles, Defines defines, Charset charset,
			DefinitionsContainer definitionsContainer, StringLocated location) {
		this.context = new TContext(importedFiles, defines, charset, definitionsContainer);
		try {
			defines.copyTo(global, location);
		} catch (EaterException e) {
			Logme.error(e);
		}
	}

	public Set<FileWithSuffix> load(List<StringLocated> list) {
//		CodeIteratorImpl.indentNow(list);
		try {
			context.executeLines(global, list, null, false);
		} catch (EaterException e) {
			context.getResultList().add(e.getLocation().withErrorPreprocessor(e.getMessage()));
			changeLastLine(context.getDebug(), e.getMessage());
			this.preprocessorError = true;
		}
		this.resultList = context.getResultList();
		this.preprocessingArtifact = context.getPreprocessingArtifact();
		return context.getFilesUsedCurrent();
	}

	private void changeLastLine(List<StringLocated> list, String message) {
		final int num = list.size() - 1;
		final StringLocated last = list.get(num);
		list.set(num, last.withErrorPreprocessor(message));
	}

	public final List<StringLocated> getResultList() {
		return resultList;
	}

	public final List<StringLocated> getDebug() {
		return context.getDebug();
	}

	public final boolean isPreprocessorError() {
		return preprocessorError;
	}

	public PreprocessingArtifact getPreprocessingArtifact() {
		return preprocessingArtifact;
	}

}
