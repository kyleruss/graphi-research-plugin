//======================================================
//  Kyle Russell
//  AUT University 2015
//  https://github.com/denkers/graphi-research-plugin
//======================================================

package com.graphi.network.rank;

import com.graphi.graph.Node;
import com.graphi.network.RankingAgent;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PolicyController 
{
    public static final int TRENDING_SOURCE_MODE    =   0;   
    public static final int TRENDING_TREE_MODE      =   1;
    
    private Set<Node> pendingInfluenceAgents;
    private Map<Node, Integer> treeScores;
    private int policyMode;
    
    public PolicyController()
    {
        pendingInfluenceAgents  =   new HashSet<>();
        treeScores              =   new HashMap<>();
    }
    
    public void pollPendingAgents()
    {
        for(Node pendingAgent: pendingInfluenceAgents)
        {
            RankingAgent agent              =   (RankingAgent) pendingAgent;
            RankingAgent optimalInfluencer  =   (RankingAgent) agent.chooseOptimalInfluencer();
            
            if(optimalInfluencer != null)
            {
                if(policyMode == TRENDING_TREE_MODE)
                {
                    RankingAgent influencerTreeRoot   =   optimalInfluencer.getTreeRootAgent();
                    if(treeScores.containsKey(influencerTreeRoot))
                        treeScores.put(influencerTreeRoot, treeScores.get(influencerTreeRoot) + 1);
                    else
                        treeScores.put(influencerTreeRoot, 1);
                }
                
                optimalInfluencer.influenceAgent(agent);
                agent.clearInfluenceOffers();
            }
        }
        
        pendingInfluenceAgents.clear();
    }
    
    public Comparator<Node> getPolicyComparator()
    {
        if(policyMode == TRENDING_TREE_MODE)
            return new TrendingTreeComparator(this);
        else 
            return new TrendingSourceComparator();
    }
    
    public void addPendingAgent(Node node)
    {
        pendingInfluenceAgents.add(node);
    }
    
    public Set<Node> getPendingInfluenceAgents()
    {
        return pendingInfluenceAgents;
    }
    
    public Map<Node, Integer> getTreeScores()
    {
        return treeScores;
    }
    
    public int getTreeScore(Node treeRootNode)
    {
        if(treeScores.containsKey(treeRootNode))
            return treeScores.get(treeRootNode);
        else
            return 0;
    }

    public int getPolicyMode() 
    {
        return policyMode;
    }

    public void setPolicyMode(int policyMode)
    {
        this.policyMode = policyMode;
    }
}
