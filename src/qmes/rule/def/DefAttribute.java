package qmes.rule.def;

import org.drools.compiler.lang.descr.AttributeDescr.Type;

public class DefAttribute extends DefBase{
//	public static enum Type {
//        STRING, NUMBER, DATE, BOOLEAN, LIST, EXPRESSION
//    }
	
	private static final long serialVersionUID = 1L;

    private String            name;
    private String            value;
    private Type              type;
    
    // default constructor for serialization
    public DefAttribute() {}

    public DefAttribute(final String name) {
        this(name,
             null, 
             Type.EXPRESSION );
    }

    public DefAttribute(final String name,
                          final String value) {
        this( name,
              value,
              Type.EXPRESSION );
    }

    public DefAttribute(final String name,
                          final String value,
                          final Type type ) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue( final String value ) {
        this.value = value;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }
    
    public String toString() {
        if( type == Type.STRING || type == Type.DATE || name.equals("dialect") ) {
            // needs escaping
            return "\""+this.value+"\"";
        }

        if(this.name.equals("timer") || this.name.equals("duration")) {
            return "("+this.value+")";
        }

        return this.value;
    }
}
