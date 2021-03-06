package com.dotcms.rekognition.actionlet;

import org.osgi.framework.BundleContext;
import com.dotmarketing.osgi.GenericBundleActivator;

public class Activator extends GenericBundleActivator {

    @Override
    public void start ( BundleContext bundleContext ) throws Exception {

        //Initializing services...
        initializeServices( bundleContext );

        //Registering the test Actionlet
        registerActionlet( bundleContext, new RekognitionActionlet() );
        System.out.println("");
        System.out.println("");
        System.out.println("Starting ");
        System.out.println("");
        System.out.println("");
    }

    @Override
    public void stop ( BundleContext bundleContext ) throws Exception {
        unregisterActionlets();
    }

}