package student.adventure;

public class Pojo {
    int x = 10;
    public class inner {
        int y = 20;
    }

    public static void main(String[] args) {
        Pojo pop = new Pojo();
        Pojo.inner inner = pop.new inner();
        System.out.println(pop.x + inner.y);
    }
}


