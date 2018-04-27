package cn.nju.edu.software.Model;

@FunctionalInterface
public interface TriConsumer<A,B,C> {
    public void apply(A a,B b,C c);
}
