//======================================================
//  Kyle Russell
//  AUT University 2015
//  https://github.com/denkers/graphi-research-plugin
//======================================================

package com.graphi.research.plugin;

import com.graphi.network.DiffusionController;
import com.graphi.network.NetworkSeeder;
import com.graphi.network.data.AbstractMeasure;
import com.graphi.network.data.MixedMeasure;
import com.graphi.network.data.PopulationMeasure;
import com.graphi.network.data.TreeMeasure;
import com.graphi.network.generator.CompleteGraphGenerator;
import com.graphi.network.generator.CycleGraphGenerator;
import com.graphi.network.generator.LineGraphGenerator;
import com.graphi.network.generator.RealDataGenerator;
import com.graphi.network.generator.StarGraphGenerator;
import com.graphi.network.generator.WheelGraphGenerator;
import com.graphi.network.rank.PolicyController;
import com.graphi.plugins.PluginManager;
import com.graphi.sim.generator.NetworkGenerator;
import com.graphi.tasks.MappedProperty;
import com.graphi.tasks.SimulateNetworkTask;
import java.io.File;

public class InitDiffusionControllerTask extends SimulateNetworkTask
{

    @Override
    public void initTaskDetails() 
    {
        setTaskName("Init Diffusion Controller");
    }

    @Override
    public void initDefaultProperties() 
    {
        //Generator
        MappedProperty genProp =   new MappedProperty();
        genProp.setName("kleinberg");
        genProp.setParamValue("latSize", "10");
        genProp.setParamValue("exp", "2");
        genProp.setParamValue("path", "/home/denker/Dropbox/FinalProjectGraphiTasks/graphs/facebook_combined.edgelist");
        setProperty("Generator", genProp.toString());
        
        //Seeding
        MappedProperty seedProp =   new MappedProperty();
        seedProp.setName("networkSeeder");
        seedProp.setParamValue("method", "0");
        seedProp.setParamValue("seedPerc", "0.1");
        seedProp.setParamValue("authMode", "true");
        seedProp.setParamValue("authPerc", "0.7");
        seedProp.setParamValue("colourAuth", "true");
        seedProp.setParamValue("colourInfl", "false");
        setProperty("Seeding", seedProp.toString());
        
        //Measure
        MappedProperty measureProp  =   new MappedProperty();
        measureProp.setName("mixedMeasure");
        measureProp.setParamValue("enable", "true");
        measureProp.setParamValue("recordMode", "2");
        measureProp.setParamValue("popSize", "0");
        setProperty("Measure", measureProp.toString());
        
        //Policy
        MappedProperty policyProp   =   new MappedProperty();
        policyProp.setName("PolicyController");
        policyProp.setParamValue("enable", "false");
        policyProp.setParamValue("policyMode", "0");
        setProperty("Policy", policyProp.toString());
        
        //Diffusion
        MappedProperty diffusionProp    =   new MappedProperty();
        diffusionProp.setName("DiffusionController");
        diffusionProp.setParamValue("diffusionMode", "0");
        diffusionProp.setParamValue("maxUnits", "100");
        diffusionProp.setParamValue("agentType", "0");
        diffusionProp.setParamValue("diffusionDecisionType", "0");
        diffusionProp.setParamValue("enableMN", "true");
        setProperty("Diffusion", diffusionProp.toString());
    }

    @Override
    public void performTask() 
    {
        ResearchPlugin plugin                       =   (ResearchPlugin) PluginManager.getInstance().getActivePlugin();
        DiffusionController diffusionController     =   getDiffusionController();
        
        plugin.setDiffusionController(diffusionController);
        diffusionController.initDiffusion();
    }
    
    public DiffusionController getDiffusionController()
    {
        String diffusionStr             =   (String) getProperty("Diffusion");
        MappedProperty diffusionProp    =   new MappedProperty(diffusionStr);
        
        int diffusionMode           =   diffusionProp.getIntParamValue("diffusionMode");
        int maxUnits                =   diffusionProp.getIntParamValue("maxUnits");
        int agentType               =   diffusionProp.getIntParamValue("agentType");
        int diffusionDecisionType   =   diffusionProp.getIntParamValue("diffusionDecisionType");
        boolean enableMN            =   diffusionProp.getBoolParamValue("enableMN");
        
        NetworkGenerator generator  =   getGenerator();
        PolicyController policy     =   getPolicy();
        AbstractMeasure measure     =   getMeasure();
        NetworkSeeder seeder        =   getSeeding();
        
        DiffusionController diffController  =   new DiffusionController(generator, seeder, diffusionMode);
        diffController.setPolicyController(policy);
        diffController.setMeasure(measure);
        diffController.setMaxUnits(maxUnits);
        diffController.setEnableMN(enableMN);
        diffController.setDiffusionDecisionType(diffusionDecisionType);
        
        if(agentType == 0)
            diffController.initInfluenceAgentManipulators();
        
        else if(agentType == 1 && policy != null)
            policy.initRankingAgentManipulators(diffusionDecisionType);
        
        return diffController;
    }
    
    
    public NetworkGenerator getGenerator()
    {
        String genAlgorithmStr  =   (String) getProperty("Generator");
        MappedProperty genProp  =   new MappedProperty(genAlgorithmStr);
        String genName          =   genProp.getName();
        NetworkGenerator gen;
        
        switch(genName)
        {
            case "berbasi": gen     =   getBASim(genProp); break;
            case "kleinberg": gen   =   getKleinbergSim(genProp); break;
            case "random": gen      =   getRASim(genProp); break;
            case "real": gen        =   getRealGenerator(genProp); break;
            default: gen            =   getClassicGenerator(genProp);
        }
        
        return gen;
    }
    
    public NetworkGenerator getClassicGenerator(MappedProperty prop)
    {
        String genName      =   prop.getName();
        int numNodes        =   prop.getIntParamValue("numNodes");
        NetworkGenerator gen;
        
        switch(genName)
        {
            case "complete": gen    =   new CompleteGraphGenerator(numNodes); break;
            case "cycle": gen       =   new CycleGraphGenerator(numNodes); break;
            case "line": gen        =   new LineGraphGenerator(numNodes); break;
            case "star": gen        =   new StarGraphGenerator(numNodes); break;
            case "wheel": gen       =   new WheelGraphGenerator(numNodes); break;
            default: gen            =   null;
        }
        
        return gen;
    }
    
    public NetworkGenerator getRealGenerator(MappedProperty prop)
    {
        String path     =   prop.getParamValue("path");
        File dataFile   =   new File(path);
        
        return new RealDataGenerator(dataFile);
    }
    
    public AbstractMeasure getMeasure()
    {
        String measureStr           =   (String) getProperty("Measure");
        MappedProperty measureProp  =   new MappedProperty(measureStr);
        String measureName          =   measureProp.getName();
        int populationSize          =   measureProp.getIntParamValue("popSize");
        int recordMode              =   measureProp.getIntParamValue("recordMode");
        boolean enableMeasure       =   measureProp.getBoolParamValue("enable");
        
        if(!enableMeasure) return null;
        
        if(measureName.equalsIgnoreCase("treeMeasure"))
            return new TreeMeasure(populationSize, recordMode);
        
        else if(measureName.equalsIgnoreCase("populationMeasure"))
            return new PopulationMeasure(populationSize, recordMode);
        
        else if(measureName.equalsIgnoreCase("mixedMeasure"))
            return new MixedMeasure();
        
        else return null;
    }
    
    public NetworkSeeder getSeeding()
    {
        String seedingStr           =   (String) getProperty("Seeding");
        MappedProperty seedingProp  =   new MappedProperty(seedingStr);
        
        int seedMethod              =   seedingProp.getIntParamValue("method");
        double seedPerc             =   seedingProp.getDoubleParamValue("seedPerc");
        boolean authMode            =   seedingProp.getBoolParamValue("authMode");
        double authPerc             =   seedingProp.getDoubleParamValue("authPerc");
        boolean colourAuth          =   seedingProp.getBoolParamValue("colourAuth");
        boolean colourInfluence     =   seedingProp.getBoolParamValue("colourInfl");
        
        NetworkSeeder seeder        =   new NetworkSeeder(seedMethod, seedPerc);
        seeder.setColourAuth(colourAuth);
        seeder.setColourInfluence(colourInfluence);
        
        if(authMode)
            seeder.enableAuthenticityMode(authPerc);
        
        return seeder;
    }
    
    public PolicyController getPolicy()
    {
        String policyStr            =   (String) getProperty("Policy");
        MappedProperty policyProp   =   new MappedProperty(policyStr);
        
        boolean enablePolicy        =   policyProp.getBoolParamValue("enable");
        
        if(!enablePolicy) return null;
        else
        {
            int policyMode  =   policyProp.getIntParamValue("policyMode");
            return new PolicyController(policyMode);
        }
    }
}
