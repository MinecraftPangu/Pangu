package cn.mccraft.pangu.core.util.resource;

import cn.mccraft.pangu.core.PanguCore;
import net.minecraft.util.ResourceLocation;

/**
 * A resource location with the domain of {@link PanguCore#ID}
 *
 * @since 1.0.1
 */
public class PanguResLoc extends ResourceLocation {
    private PanguResLoc(String resourcePathIn) {
        super(PanguCore.ID, resourcePathIn);
    }

    public static PanguResLoc of(String resourcePathIn) {
        return new PanguResLoc(resourcePathIn);
    }

    public static ResourceLocation of(String domain, String resourcePathIn) {
        return new ResourceLocation(domain, resourcePathIn);
    }
}
