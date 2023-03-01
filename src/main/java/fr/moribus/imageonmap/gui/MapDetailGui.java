/*
 * Copyright or © or Copr. Moribus (2013)
 * Copyright or © or Copr. ProkopyL <prokopylmc@gmail.com> (2015)
 * Copyright or © or Copr. Amaury Carrade <amaury@carrade.eu> (2016 – 2021)
 * Copyright or © or Copr. Vlammar <valentin.jabre@gmail.com> (2019 – 2021)
 *
 * This software is a computer program whose purpose is to allow insertion of
 * custom images in a Minecraft world.
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package fr.moribus.imageonmap.gui;

import de.rapha149.signgui.SignGUI;
import fr.moribus.imageonmap.Permissions;
import fr.moribus.imageonmap.i18n.I;
import fr.moribus.imageonmap.map.ImageMap;
import fr.moribus.imageonmap.map.PosterMap;
import fr.moribus.imageonmap.map.SingleMap;
import fr.moribus.imageonmap.ui.MapItemManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class MapDetailGui extends ExplorerGui<Integer> {
    private final ImageMap map;
    private final OfflinePlayer offplayer;
    private final String name;

    public MapDetailGui(ImageMap map, OfflinePlayer p, String name) {
        super();
        this.map = map;
        this.offplayer = p;
        this.name = name;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected ItemStack getViewItem(int x, int y) {
        final Material partMaterial = y % 2 == x % 2 ? Material.MAP : Material.PAPER;

        ItemStack item = new ItemStack(partMaterial);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(I.t(getPlayerLocale(), "{green}Map part"));
        List<String> lore = new ArrayList<>(Arrays.asList(
                I.t(getPlayerLocale(), "{gray}Row: {white}{0}", y + 1),
                I.t(getPlayerLocale(), "{gray}Column: {white}{0}", x + 1)
        ));

        if (Permissions.GET.grantedTo(getPlayer())) {
            lore.add("");
            lore.add(I.t(getPlayerLocale(), "{gray}» {white}Click{gray} to get only this part"));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected ItemStack getViewItem(Integer mapId) {
        final int index = ((PosterMap) map).getIndex(mapId);
        final Material partMaterial = index % 2 == 0 ? Material.MAP : Material.PAPER;

        ItemStack item = new ItemStack(partMaterial);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(I.t(getPlayerLocale(), "{green}Map part"));
        List<String> lore = new ArrayList<>(List.of(
                I.t(getPlayerLocale(), "{gray}Part: {white}{0}", index + 1)
        ));

        if (Permissions.GET.grantedTo(getPlayer())) {
            lore.add("");
            lore.add(I.t(getPlayerLocale(), "{gray}» {white}Click{gray} to get only this part"));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected ItemStack getPickedUpItem(int x, int y) {
        if (!Permissions.GET.grantedTo(getPlayer())) {
            return null;
        }

        if (map instanceof SingleMap) {
            return MapItemManager.createMapItem((SingleMap) map, true);
        } else if (map instanceof PosterMap) {
            return MapItemManager.createMapItem((PosterMap) map, x, y);
        }

        throw new IllegalStateException("Unsupported map type: " + map.getType());
    }

    @Override
    protected ItemStack getPickedUpItem(Integer mapId) {
        if (!Permissions.GET.grantedTo(getPlayer())) {
            return null;
        }

        final PosterMap poster = (PosterMap) map;
        return MapItemManager.createMapItem(poster, poster.getIndex(mapId));
    }

    @Override
    protected ItemStack getEmptyViewItem() {
        if (map instanceof SingleMap) {
            return getViewItem(0, 0);
        } else {
            return super.getEmptyViewItem();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onUpdate() {
        /// Title of the map details GUI
        if (offplayer.getUniqueId().equals(getPlayer().getUniqueId())) {
            setTitle(I.t(getPlayerLocale(), "Your maps » {black}{0}", map.getName()));
        } else {
            setTitle(I.t(getPlayerLocale(), "{1}'s maps » {black}{0}", map.getName(), name));
        }
        setKeepHorizontalScrollingSpace(true);

        if (map instanceof PosterMap) {
            PosterMap poster = (PosterMap) map;
            if (poster.hasColumnData()) {
                setDataShape(poster.getColumnCount(), poster.getRowCount());
            } else {
                setData(Arrays.stream(map.getMapsIDs()).boxed().toArray(Integer[]::new));
            }
        } else {
            setDataShape(1, 1);
        }

        final boolean canRename = Permissions.RENAME.grantedTo(getPlayer());
        final boolean canDelete = Permissions.DELETE.grantedTo(getPlayer());

        int renameSlot = getSize() - 7;
        int deleteSlot = getSize() - 6;

        if (!canRename) {
            deleteSlot--;
        }

        if (canRename) {
            ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(I.t(getPlayerLocale(), "{blue}Rename this image"));
            meta.setLore(GuiUtils.generateLore(I.t(getPlayerLocale(),
                    "{gray}Click here to rename this image; this is used for your own organization.")));
            item.setItemMeta(meta);
            action("rename", renameSlot, item);
        }

        if (canDelete) {
            ItemStack item = new ItemStack(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(I.t(getPlayerLocale(), "{red}Delete this image"));
            List<String> lore = GuiUtils.generateLore(I.t(getPlayerLocale(),
                    "{gray}Deletes this map {white}forever{gray}. This action cannot be undone!"));
            lore.add("");
            lore.addAll(GuiUtils.generateLore(I.t(getPlayerLocale(),
                    "{gray}You will be asked to confirm your choice if you click here.")));
            meta.setLore(lore);
            item.setItemMeta(meta);
            action("delete", deleteSlot, item);
        }


        // To keep the controls centered, the back button is shifted to the right when the
        // arrow isn't displayed, so when the map fit on the grid without sliders.
        int backSlot = getSize() - 4;

        if (!canRename && !canDelete) {
            backSlot = getSize() - 5;
        } else if (map instanceof PosterMap && ((PosterMap) map).getColumnCount() <= INVENTORY_ROW_SIZE) {
            backSlot++;
        }

        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(I.t(getPlayerLocale(), "{green}« Back"));
        meta.setLore(new ArrayList<>(List.of(I.t(getPlayerLocale(), "{gray}Go back to the list."))));
        item.setItemMeta(meta);
        action("back", backSlot, item);
    }

    @GuiAction("rename")
    public void rename() {
        if (!Permissions.RENAME.grantedTo(getPlayer())) {
            I.sendT(getPlayer(), "{ce}You are no longer allowed to do that.");
            update();
            return;
        }
        SignGUI prompt = new SignGUI().lines(map.getName())
                .onFinish((player, lines) -> {
                    StringBuilder name = new StringBuilder();
                    for (String line : lines) {
                        if (line != null && !line.trim().isEmpty()) {
                            if(name.length() > 0) {
                                name.append(" ");
                            }
                            name.append(line);
                        }
                        if (name.length() == 0) {
                            player.sendMessage("Sign can't be empty!"); // TODO: find way to put this into SignGUI
                            return lines;
                        }
                    }
                    if (name.toString().equals(map.getName())) {
                        close();
                        return null;
                    }
                    map.rename(name.toString());
                    close();
                    return null;
                });
        prompt.open(getPlayer());
    }

    @GuiAction("delete")
    public void delete() {
        if (!Permissions.DELETE.grantedTo(getPlayer())) {
            I.sendT(getPlayer(), "{ce}You are no longer allowed to do that.");
            update();
            return;
        }
        Gui.open(getPlayer(), new ConfirmDeleteMapGui(map), this);
    }

    @GuiAction("back")
    public void back() {
        close();
    }
}
