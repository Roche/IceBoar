/*
 * ****************************************************************************
 *  Copyright © 2015 Hoffmann-La Roche
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ****************************************************************************
 */

package com.roche.iceboar.runner;

import com.roche.iceboar.progressevent.ProgressEvent;
import com.roche.iceboar.progressevent.ProgressEventFactory;
import com.roche.iceboar.progressevent.ProgressEventQueue;
import com.roche.iceboar.settings.GlobalSettings;

/**
 * Runs a target user application on JVM same as JNLP were run.
 */
public class CurrentJVMRunner extends AbstractJVMRunner {

    public CurrentJVMRunner(GlobalSettings settings, ExecutableCommandFactory executableCommandFactory,
                            ProgressEventFactory progressEventFactory, ProgressEventQueue progressEventQueue) {
        super(settings, progressEventQueue, progressEventFactory, executableCommandFactory);
    }

    public void runOnJVM() {
        runMainClass();
    }

    @Override
    protected void runMainClass() {
        ExecutableCommand command = executableCommandFactory.createRunTargetApplicationCommand(
                settings, settings.getCurrentJavaCommand());

        Process process = command.exec();

        redirectProcessOutputsToDebugWindow(process);
        progressEventQueue.update(progressEventFactory.getAppStartedEvent());
    }

    public void update(ProgressEvent event) {
        if (event.equals(progressEventFactory.getAppStartingEvent())) {
            runOnJVM();
        }
    }
}
