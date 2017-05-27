//======================================================
//  Kyle Russell
//  AUT University 2015
//  https://github.com/denkers/graphi-research-plugin
//======================================================

package com.graphi.research.plugin;

import com.graphi.plugins.AbstractPlugin;

public class ResearchPlugin extends AbstractPlugin
{
    public static final String PLUGIN_NAME        =   "AUT Research Plugin";
    public static final String PLUGIN_DESCRIPTION =   "Agent-based network diffusion research plugin";   

    public ResearchPlugin()
    {
        displayHandler  =   new ResearchDisplayHandler();
    }
    
    @Override
    public void onEvent(int i)
    {
        super.onEvent(i);
    }

    @Override
    public void initPluginDetails() 
    {
        name        =   PLUGIN_NAME;
        description =   PLUGIN_DESCRIPTION;
    }

    @Override
    public void onPluginLoad() {}

    @Override
    public void onPluginActivate() {}

    @Override
    public void onPluginDeactivate() {}
}