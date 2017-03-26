package net.atomation.rgbdemo.services.atomation;

import net.atomation.rgbdemo.interfaces.IAppScanListener;
import net.atomation.rgbdemo.services.IServiceCommand;

/**
 * a collection of all the commands supported by AtomationService
 * NOTE: all commands will be executed on a worker thread
 * Created by eyal on 09/01/2017.
 */

/* package */ final class AtomationServiceCommands {

    /* package */ static final class StartScanCommand implements IServiceCommand<AtomationService> {

        @Override
        public void execute(AtomationService service) {
            service.startScan();
        }
    }

    /* package */ static final class StopScanCommand implements IServiceCommand<AtomationService> {

        @Override
        public void execute(AtomationService service) {
            service.stopScan();
        }
    }

    /* package */ static final class AddScanListenerCommand implements IServiceCommand<AtomationService> {

        private final IAppScanListener listener;

        /* package */ AddScanListenerCommand(IAppScanListener listener) {
            this.listener = listener;
        }

        @Override
        public void execute(AtomationService service) {
            service.addScanListener(listener);
        }
    }

    /* package */ static final class RemoveScanListenerCommand implements IServiceCommand<AtomationService> {

        private final IAppScanListener listener;

        /* package */ RemoveScanListenerCommand(IAppScanListener listener) {
            this.listener = listener;
        }

        @Override
        public void execute(AtomationService service) {
            service.removeScanListener(listener);
        }
    }
}
