import static java.lang.String.format 

class SomeClass {

    String format(Integer i) { 
        i.toString()
    }

    static void main(String[] args) {
        assert format('String') == 'String' 
        assert new SomeClass().format(Integer.valueOf(1)) == '1'
    }
}