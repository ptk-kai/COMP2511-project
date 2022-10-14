package dungeonmania.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;

public class CopyUtil {
    
    public static <T extends Entity> Entity deepCopyObject(T ent) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(ent);
            oos.close();
            bos.close();
            byte[] byteArray = bos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
            Entity copiedEntity = (Entity) new ObjectInputStream(bais).readObject();
            if (!ent.getId ().contains("past")) {
                copiedEntity.setId(ent.getId() + "_past");
            }
            if (ent instanceof Player) {
                copiedEntity.setType(DungeonMacro.OLD_PLAYER);
            }
            return copiedEntity;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
