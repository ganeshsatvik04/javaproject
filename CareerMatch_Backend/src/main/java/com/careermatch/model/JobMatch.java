package com.careermatch.model;

public class JobMatch {
    private final Job job;
    private final double matchPercentage;

    public JobMatch(Job job, double matchPercentage) {
        this.job = job;
        this.matchPercentage = matchPercentage;
    }

    public Job getJob() { return job; }
    public double getMatchPercentage() { return matchPercentage; }
}