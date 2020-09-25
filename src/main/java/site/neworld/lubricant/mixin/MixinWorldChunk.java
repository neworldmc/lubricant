package site.neworld.lubricant.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.collection.TypeFilterableList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.function.Predicate;

@Mixin(WorldChunk.class)
public class MixinWorldChunk {
    @Shadow
    private TypeFilterableList<Entity>[] entitySections;

    @Overwrite
    public void collectOtherEntities(Entity except, Box box, List<Entity> entityList, Predicate<? super Entity> predicate) {
        int i = MathHelper.floor((box.minY - 2.0D) / 16.0D);
        int j = MathHelper.floor((box.maxY + 2.0D) / 16.0D);
        i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySections.length - 1);

        for(int k = i; k <= j; ++k) {
            for (var entity : this.entitySections[k]) {
                if (entity.getBoundingBox().intersects(box) && entity != except) {
                    if (predicate == null || predicate.test(entity)) entityList.add(entity);
                    if (entity instanceof EnderDragonEntity) {
                        for (var part : ((EnderDragonEntity) entity).getBodyParts()) {
                            if (part != except && part.getBoundingBox().intersects(box) && (predicate == null || predicate.test(part))) {
                                entityList.add(part);
                            }
                        }
                    }
                }
            }
        }
    }

    @Overwrite
    public <T extends Entity> void collectEntities(EntityType<?> type, Box box, List<? super T> result, Predicate<? super T> predicate) {
        int i = MathHelper.floor((box.minY - 2.0D) / 16.0D);
        int j = MathHelper.floor((box.maxY + 2.0D) / 16.0D);
        i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySections.length - 1);
        for(int k = i; k <= j; ++k) {
            for (var entity: this.entitySections[k]) {
                if (type != null && entity.getType() != type) continue;
                if (entity.getBoundingBox().intersects(box) && predicate.test((T)entity)) {
                    result.add((T)entity);
                }
            }
        }
    }

    @Overwrite
    public <T extends Entity> void collectEntitiesByClass(Class<? extends T> entityClass, Box box, List<T> result, Predicate<? super T> predicate) {
        int i = MathHelper.floor((box.minY - 2.0D) / 16.0D);
        int j = MathHelper.floor((box.maxY + 2.0D) / 16.0D);
        i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySections.length - 1);

        for(int k = i; k <= j; ++k) {
            for (var entity: this.entitySections[k].getAllOfType(entityClass)) {
                if (!entity.getBoundingBox().intersects(box)) continue;
                if (predicate != null && !predicate.test(entity)) continue;
                result.add(entity);
            }
        }
    }
}
