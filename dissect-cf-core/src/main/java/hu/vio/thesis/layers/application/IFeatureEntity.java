package hu.vio.thesis;

interface IFeatureEntity<T> {
    T compute(ComputingNode computingNode);
}