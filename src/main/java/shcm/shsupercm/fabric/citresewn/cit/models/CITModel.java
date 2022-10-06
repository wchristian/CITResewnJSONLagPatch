package shcm.shsupercm.fabric.citresewn.cit.models;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

public final class CITModel {
    public final Identifier propertiesIdentifier;
    private JsonUnbakedModel mainModel = null;
    public BakedModel bakedModel = null;

    public CITModel(Identifier propertiesIdentifier) {
        this.propertiesIdentifier = propertiesIdentifier;
    }

    public JsonUnbakedModel mainModel() {
        return this.mainModel;
    }

    public CITModel mainModel(JsonUnbakedModel unbakedModel) throws ModelReadException {
        if (this.mainModel != null)
            throw new ModelReadException(new IllegalStateException("Already set main model"));

        this.mainModel = unbakedModel;
        ((ModelWithCITModel) this.mainModel).citresewn$setCITModel(this);
        return this;
    }

    public CITModel mainModel(Reader json) throws ModelReadException {
        try {
            return mainModel(JsonUnbakedModel.deserialize(json));
        } catch (Exception e) {
            throw new ModelReadException(e);
        }
    }

    public CITModel mainModel(Identifier absoluteLocation, ResourceManager resourceManager) throws ModelReadException {
        try {
            BufferedReader reader = resourceManager.getResource(absoluteLocation).orElseThrow().getReader();
            return mainModel(reader);
        } catch (IOException | NoSuchElementException e) {
            throw new ModelReadException(e);
        }
    }

    public CITModel mainModel(Item baseModel, ResourceManager resourceManager) throws ModelReadException {
        Identifier id = Registry.ITEM.getId(baseModel);
        if (Registry.ITEM.getDefaultId().equals(id))
            throw new ModelReadException(new IllegalStateException("Item not registered"));
        id = new Identifier(id.getNamespace(), "models/" + id.getPath() + ".json");

        return mainModel(id, resourceManager);
    }
}
