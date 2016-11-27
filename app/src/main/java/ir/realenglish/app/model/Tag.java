package ir.realenglish.app.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by ALI on 7/1/2016.
 */
public class Tag  implements Serializable {
    @Expose
    public String name;
    @Expose
    public int id;
/*
    public List<Idiom> idioms() {
        return new Select()
                .from(Idiom.class)
                .innerJoin(IdiomTag.class).on("Idioms.id = Idiom_Tag.id")
                .where("Idiom_Tag.Tag = ?", this.getId())
                .execute();
    }
*/
    @Override
    public boolean equals(Object obj) {
        try {
            if (obj != null && obj instanceof Tag) {
                return this.name.equalsIgnoreCase(((Tag) obj).name);
            }
        } catch (Exception e) {
        }
        return super.equals(obj);
    }

}
