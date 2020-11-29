/*
 * C3338047
 * Yuji Ishikawa
 * Purpose: store Process page data and some related values
 * */

public class Process {
    private String name;
    private int page, penalty, LRUtime, used;
    private boolean inMain;


    public Process(String name, int page)
    {
        this.name = name;
        this.page = page;
        this.inMain = false;
    }

    //getter and setter sections for process
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPage() { return page; }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isInMain() {
        return inMain;
    }

    public void setInMain(boolean inMain) {
        this.inMain = inMain;
    }

    public int getPenalty() { return penalty; }

    public void setPenalty(int penalty) { this.penalty = penalty; }

    public int getLRUtime() { return LRUtime; }

    public void setLRUtime(int LRUtime) { this.LRUtime = LRUtime; }

    public int getUsed() { return used; }

    public void setUsed(int used) { this.used = used; }
//    @Override
//    public void swap(ArrayList )
//    {
//
//    }
}
